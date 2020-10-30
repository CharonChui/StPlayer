package com.st.preload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.st.preload.cache.CacheListener;
import com.st.preload.server.HttpProxyCacheServer;
import com.st.preload.utils.ProxyCacheUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PreloadManager {
    private static final String LOG_TAG = PreloadManager.class.getSimpleName();
    private HttpProxyCacheServer httpProxyCacheServer;
    private static final long DEFAULT_MAX_SIZE = 600 * 1024 * 1024; //最大缓存容量
    public static int DEFAULT_MAX_FILE_COUNT = 50; //最大缓存数量

    /**
     * 单线程池，按照添加顺序依次执行{@link PreloadTask}
     */
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    /**
     * 保存正在预加载的{@link PreloadTask}
     */
    private final LinkedHashMap<String, PreloadTask> mPreloadTasks = new LinkedHashMap<>();

    private PreloadManager() {
    }

    private static class VideoProxyManagerHolder {
        private static final PreloadManager preloadManager = new PreloadManager();
    }

    public static PreloadManager getInstance() {
        return VideoProxyManagerHolder.preloadManager;
    }

    public void init(Context context, File cacheFile, int maxFileCount, long maxFileSize) {
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context).maxCacheSize(DEFAULT_MAX_SIZE)
                .maxCacheFilesCount(maxFileCount > 0 ? maxFileCount : DEFAULT_MAX_FILE_COUNT)
                .maxCacheSize(maxFileSize > 0 ? maxFileSize : DEFAULT_MAX_SIZE);
        if (cacheFile != null) {
            builder.cacheDirectory(cacheFile);
        }
        httpProxyCacheServer = builder.build();

    }

    public void prefetch(final String videoUrl, final int prefetchSize) {
        if (isPreloaded(videoUrl, prefetchSize)) {
            return;
        }
        PreloadTask task = new PreloadTask(videoUrl, httpProxyCacheServer, prefetchSize);
        mPreloadTasks.put(videoUrl, task);
        task.executeOn(mExecutorService);
    }

    public void cancelPrefetch(String videoUrl) {
        PreloadTask task = mPreloadTasks.get(videoUrl);
        if (task != null) {
            task.cancel();
            mPreloadTasks.remove(videoUrl);
        }
    }

    public void cancelAllPrefetch() {
        Iterator<Map.Entry<String, PreloadTask>> iterator = mPreloadTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PreloadTask> next = iterator.next();
            PreloadTask task = next.getValue();
            task.cancel();
            iterator.remove();
        }
    }

    /**
     * 判断该播放地址是否已经预加载
     */
    private boolean isPreloaded(String rawUrl, long preloadSize) {
        //先判断是否有缓存文件，如果已经存在缓存文件，并且其大小大于1KB，则表示已经预加载完成了
        File cacheFile = httpProxyCacheServer.getCacheFile(rawUrl);
        if (cacheFile.exists()) {
            if (cacheFile.length() >= 1024) {
                return true;
            } else {
                //这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete();
                return false;
            }
        }
        //再判断是否有临时缓存文件，如果已经存在临时缓存文件，并且临时缓存文件超过了预加载大小，则表示已经预加载完成了
        File tempCacheFile = httpProxyCacheServer.getTempCacheFile(rawUrl);
        if (tempCacheFile.exists()) {
            return tempCacheFile.length() >= preloadSize;
        }

        return false;
    }

    /**
     * 传给播放器的url替换成代理的url
     **/

    public String getProxyUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        PreloadTask task = mPreloadTasks.get(url);
        if (task != null) {
            task.cancel();
        }
        return httpProxyCacheServer.getProxyUrl(url);
    }

    public void shutdown() {
        httpProxyCacheServer.shutdown();
    }

    public void registerCacheListener(CacheListener cacheListener, String url) {
        httpProxyCacheServer.registerCacheListener(cacheListener, url);
    }


    class PreloadTask implements Runnable {
        private final String mRawUrl;
        private final HttpProxyCacheServer mHttpProxyCacheServer;
        private final long mNeedPreloadSize;

        public PreloadTask(@NonNull String url, @NonNull HttpProxyCacheServer server, long preloadSize) {
            mRawUrl = url;
            mHttpProxyCacheServer = server;
            mNeedPreloadSize = preloadSize;
        }

        /**
         * 是否被取消
         */
        private boolean mIsCanceled;

        /**
         * 是否正在预加载
         */
        private boolean mIsExecuted;

        @Override
        public void run() {
            if (!mIsCanceled) {
                start();
            }
            mIsExecuted = false;
            mIsCanceled = false;
        }

        /**
         * 开始预加载
         */
        private void start() {
            if (TextUtils.isEmpty(mRawUrl)) {
                return;
            }
            HttpURLConnection connection = null;
            try {
                //获取HttpProxyCacheServer的代理地址
                String proxyUrl = mHttpProxyCacheServer.getProxyUrl(mRawUrl);
                Log.e(LOG_TAG, "proxy url : " + proxyUrl);
                URL url = new URL(proxyUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("ST_PREFETCH_MAX_SIZE", String.valueOf(mNeedPreloadSize));
                connection.connect();
                Log.e(LOG_TAG, "range : " + mNeedPreloadSize);
                InputStream in = new BufferedInputStream(connection.getInputStream());
                int length;
                int read = 0;
                byte[] bytes = new byte[ProxyCacheUtils.DEFAULT_BUFFER_SIZE];
                while ((length = in.read(bytes)) != -1) {
                    read += length;
                    if (read >= mNeedPreloadSize) {
                        Log.e(LOG_TAG, "end prefetch");
                        break;
                    }
                }
                if (read == -1) { //这种情况一般是预加载出错了，删掉缓存
                    File cacheFile = mHttpProxyCacheServer.getCacheFile(mRawUrl);
                    if (cacheFile.exists()) {
                        cacheFile.delete();
                    }
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        /**
         * 将预加载任务提交到线程池，准备执行
         */
        public void executeOn(ExecutorService executorService) {
//            if (mIsExecuted) {
//                return;
//            }
            mIsExecuted = true;
            executorService.submit(this);
        }

        /**
         * 取消预加载任务
         */
        public void cancel() {
            if (mIsExecuted) {
                mIsCanceled = true;
            }
        }
    }
}
