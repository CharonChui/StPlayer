package com.st.preload.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.st.preload.GetRequest;
import com.st.preload.IgnoreHostProxySelector;
import com.st.preload.cache.CacheListener;
import com.st.preload.config.Config;
import com.st.preload.disk.IDiskUsage;
import com.st.preload.disk.TotalCountLruDiskUsage;
import com.st.preload.disk.TotalSizeLruDiskUsage;
import com.st.preload.exception.ProxyCacheException;
import com.st.preload.filename.IFileNameGenerator;
import com.st.preload.filename.Md5FileNameGenerator;
import com.st.preload.headers.EmptyHeadersInjector;
import com.st.preload.headers.IHeaderInjector;
import com.st.preload.ping.Pinger;
import com.st.preload.sourcestorage.SourceInfoStorage;
import com.st.preload.sourcestorage.SourceInfoStorageFactory;
import com.st.preload.utils.Preconditions;
import com.st.preload.utils.ProxyCacheUtils;
import com.st.preload.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpProxyCacheServer {
    private static final String LOG_TAG = HttpProxyCacheServer.class.getName();
    private static final String PROXY_HOST = "127.0.0.1";

    private Config mConfig;
    private ServerSocket mServerSocket;
    private int mPort;
    private Thread mWaitConnectionThread;
    private Pinger mPinger;
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);
    private final Map<String, HttpProxyCacheServerClients> clientsMap = new ConcurrentHashMap<>();
    private final Object clientsLock = new Object();

    public HttpProxyCacheServer(Context context) {
        this(new Builder(context).buildConfig());
    }

    public HttpProxyCacheServer(@NonNull Config config) {
        if (config == null) {
            return;
        }
        mConfig = config;
        try {
            InetAddress inetAddress = InetAddress.getByName(PROXY_HOST);
            // 建立一个local的ServerSocket，port参数为0表示随机分配端口号
            mServerSocket = new ServerSocket(0, 8, inetAddress);
            mPort = mServerSocket.getLocalPort();
            //创建代理选择器，从而如果是其他网络请求，继续经过原来的代理，如果是自己设置的ServerSocket,则不会走系统代理
            IgnoreHostProxySelector.install(PROXY_HOST, mPort);
            // countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
            // 是通过一个计数器来实现的，计数器的初始值是线程的数量。每当一个线程执行完毕后，
            // 计数器的值就-1，当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以
            // 恢复工作了。
            CountDownLatch startSignal = new CountDownLatch(1);
            // 该Thread的作用就是等待客户端socket连接，然后通过线程池去处理每一个socket
            mWaitConnectionThread = new Thread(new WaitRequestsRunnable(startSignal));
            mWaitConnectionThread.start();
            startSignal.await(); // freeze thread, wait for server starts
            // 上面这部分代码的意思就是等待WaitRequestsRunnable中的run方法执行完后再执行下面的内容
            mPinger = new Pinger(PROXY_HOST, mPort);
            Log.i(LOG_TAG, "Proxy cache server start");
        } catch (IOException | InterruptedException e) {
            socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }
    }

    /**
     * Returns url that wrap original url and should be used for client (MediaPlayer, ExoPlayer, etc).
     * <p>
     * If file for this url is fully cached (it means method {@link #isCached(String)} returns {@code true})
     * then file:// uri to cached file will be returned.
     * <p>
     * Calling this method has same effect as calling {@link #getProxyUrl(String, boolean)} with 2nd parameter set to {@code true}.
     *
     * @param url a url to file that should be cached.
     * @return a wrapped by proxy url if file is not fully cached or url pointed to cache file otherwise.
     */
    public String getProxyUrl(String url) {
        return getProxyUrl(url, true);
    }

    /**
     * Returns url that wrap original url and should be used for client (MediaPlayer, ExoPlayer, etc).
     * <p>
     * If parameter {@code allowCachedFileUri} is {@code true} and file for this url is fully cached
     * (it means method {@link #isCached(String)} returns {@code true}) then file:// uri to cached file will be returned.
     *
     * @param url                a url to file that should be cached.
     * @param allowCachedFileUri {@code true} if allow to return file:// uri if url is fully cached
     * @return a wrapped by proxy url if file is not fully cached or url pointed to cache file otherwise (if {@code allowCachedFileUri} is {@code true}).
     */
    public String getProxyUrl(String url, boolean allowCachedFileUri) {
        if (allowCachedFileUri && isCached(url)) {
            File cacheFile = getCacheFile(url);
            // 更新一下文件最后的修改时间，这是为了防止时间太久被Lru缓存清除
            touchFileSafely(cacheFile);
            return Uri.fromFile(cacheFile).toString();
        }
        // 如果代理服务器还在运行就返回代理地址，否则直接返回原url
        Log.e(LOG_TAG, "isAlive : " + isAlive());
        return isAlive() ? appendToProxyUrl(url) : url;
    }

    public File getTempCacheFile(String url) {
        File cacheDir = mConfig.getCacheRoot();
        String fileName = mConfig.getFileNameGenerator().generate(url) + ".download";
        return new File(cacheDir, fileName);
    }

    public File getCacheRoot() {
        return mConfig.getCacheRoot();
    }

    public File getCacheFile(String url) {
        File cacheDir = mConfig.getCacheRoot();
        String fileName = mConfig.getFileNameGenerator().generate(url);
        return new File(cacheDir, fileName);
    }

    private void touchFileSafely(File cacheFile) {
        try {
            mConfig.getDiskUsage().touch(cacheFile);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error touching file " + cacheFile + e);
        }
    }

    // 将原Url拼接到一个  http://127.0.0.1:xxx/Url 即可
    private String appendToProxyUrl(String url) {
        // 为啥要encode，不然http://127.0.0.1:xxx/http://xxxxx.mp4这样肯定有问题，所以要encode下
        return String.format(Locale.US, "http://%s:%d/%s", PROXY_HOST, mPort, ProxyCacheUtils.encode(url));
    }

    public void registerCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(cacheListener, url);
        synchronized (clientsLock) {
            try {
                getClients(url).registerCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                Log.e(LOG_TAG, "Error registering cache listener" + e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(cacheListener, url);
        synchronized (clientsLock) {
            try {
                getClients(url).unregisterCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                Log.e(LOG_TAG, "Error registering cache listener" + e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener) {
        Preconditions.checkNotNull(cacheListener);
        synchronized (clientsLock) {
            for (HttpProxyCacheServerClients clients : clientsMap.values()) {
                clients.unregisterCacheListener(cacheListener);
            }
        }
    }

    /**
     * Checks is cache contains fully cached file for particular url.
     *
     * @param url an url cache file will be checked for.
     * @return {@code true} if cache contains fully cached file for passed in parameters url.
     */
    public boolean isCached(String url) {
        Preconditions.checkNotNull(url, "Url can't be null!");
        return getCacheFile(url).exists();
    }

    public void shutdown() {
        Log.i(LOG_TAG, "Shutdown proxy server");

        shutdownClients();

        mConfig.getSourceInfoStorage().release();

        mWaitConnectionThread.interrupt();
        try {
            if (!mServerSocket.isClosed()) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error shutting down proxy server" + e);
        }
    }

    private void shutdownClients() {
        synchronized (clientsLock) {
            for (HttpProxyCacheServerClients clients : clientsMap.values()) {
                clients.shutdown();
            }
            clientsMap.clear();
        }
    }

    private boolean isAlive() {
        return mPinger.ping(3, 70);   // 70+140+280=max~500ms
    }


    private final class WaitRequestsRunnable implements Runnable {

        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

        @Override
        public void run() {
            startSignal.countDown();
            waitForRequest();
        }
    }

    private void waitForRequest() {
        try {
            // 只要不被打断就是哥死循环
            while (!Thread.currentThread().isInterrupted()) {
                // 不断等待接收ServerSocket接收到的内容
                Socket socket = mServerSocket.accept();
                // 用线程池去处理每一个接收到的内容
                socketProcessor.submit(new SocketProcessorRunnable(socket));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error during waiting connection : " + e.toString());
        }
    }

    private final class SocketProcessorRunnable implements Runnable {
        private final Socket socket;

        public SocketProcessorRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            processSocket(socket);
        }
    }

    private void processSocket(Socket socket) {
        try {
            // 从客户端的Socket获取输入流，然后创建一个GetRequest
            GetRequest request = GetRequest.read(socket.getInputStream());
            String url = ProxyCacheUtils.decode(request.uri);
            if (mPinger.isPingRequest(url)) {
                // 如果播放器发的是一个ping的请求，就直接返回200 ok，因为在判断播放器是否存活的时候会ping一下来判断
                mPinger.responseToPing(socket);
            } else {
                // 不是ping而是真实的视频url，通过url获取一个处理的Client，如果没有就新new一个
                HttpProxyCacheServerClients clients = getClients(url);
                // 通过Client处理请求request，socket
                clients.processRequest(request, socket);
            }
        } catch (SocketException e) {
            // There is no way to determine that client closed connection http://stackoverflow.com/a/10241044/999458
            // So just to prevent log flooding don't log stacktrace
            Log.e(LOG_TAG, "Closing socket… Socket is closed by client.");
        } catch (ProxyCacheException | IOException e) {
            Log.e(LOG_TAG, "Error processing request" + e);
        } finally {
            releaseSocket(socket);
        }
    }

    private void releaseSocket(Socket socket) {
        closeSocketInput(socket);
        closeSocketOutput(socket);
        closeSocket(socket);
    }

    private void closeSocketInput(Socket socket) {
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }
        } catch (SocketException e) {
            // There is no way to determine that client closed connection http://stackoverflow.com/a/10241044/999458
            // So just to prevent log flooding don't log stacktrace
            Log.e(LOG_TAG, "Releasing input stream… Socket is closed by client.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocketOutput(Socket socket) {
        try {
            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to close socket on proxy side: {}. It seems client have already closed connection." + e.getMessage());
        }
    }

    private void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing socket" + e);
        }
    }

    private HttpProxyCacheServerClients getClients(String url) throws ProxyCacheException {
        HttpProxyCacheServerClients result;
        synchronized (clientsLock) {
            // 先从内存缓存map中看有没有该url对应的client，如果没有就new一个
            // HttpProxyCacheServerClients就是用来处理一个url请求的处理工作
            HttpProxyCacheServerClients clients = clientsMap.get(url);
            if (clients == null) {
                clients = new HttpProxyCacheServerClients(url, mConfig);
                clientsMap.put(url, clients);
            }
            result = clients;
        }
        return result;
    }

    public static final class Builder {
        private static final long DEFAULT_MAX_SIZE = 512 * 1024 * 1024;
        // 缓存目录
        private File mCacheRoot;
        // 文件名生成器
        private IFileNameGenerator mFileNameGenerator;
        // 缓存控制策略
        private IDiskUsage mDiskUsage;
        // 请求header
        private IHeaderInjector mHeaderInjector;
        // 存储方式，默认是数据库存储
        private final SourceInfoStorage mSourceInfoStorage;

        public Builder(Context context) {
            mCacheRoot = StorageUtils.getIndividualCacheDirectory(context);
            mDiskUsage = new TotalSizeLruDiskUsage(DEFAULT_MAX_SIZE);
            mFileNameGenerator = new Md5FileNameGenerator();
            mHeaderInjector = new EmptyHeadersInjector();
            mSourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(context);
        }

        /**
         * 设置缓存目录
         */
        public HttpProxyCacheServer.Builder cacheDirectory(@NonNull File file) {
            if (file != null) {
                mCacheRoot = file;
            }
            return this;
        }

        /**
         * 自定义文件名称生成器，默认是使用@{@link Md5FileNameGenerator}生成
         */
        public HttpProxyCacheServer.Builder fileNameGenerator(@NonNull IFileNameGenerator fileNameGenerator) {
            if (fileNameGenerator != null) {
                mFileNameGenerator = fileNameGenerator;
            }
            return this;
        }

        /**
         * 设置最大的缓冲大小，如不设置，默认为512M
         *
         * @param maxSize 单位为byte，
         */
        public HttpProxyCacheServer.Builder maxCacheSize(long maxSize) {
            mDiskUsage = new TotalSizeLruDiskUsage(maxSize);
            return this;
        }

        /**
         * 设置最大的缓冲文件个数
         */
        public HttpProxyCacheServer.Builder maxCacheFilesCount(int count) {
            mDiskUsage = new TotalCountLruDiskUsage(count);
            return this;
        }

        /**
         * 自定义IDiskUsage来控制如何保存及清理缓存
         */
        public HttpProxyCacheServer.Builder diskUsage(@NonNull IDiskUsage diskUsage) {
            if (diskUsage != null) {
                mDiskUsage = diskUsage;
            }
            return this;
        }

        /**
         * 请求时增加的header信息
         */
        public HttpProxyCacheServer.Builder headerInjector(IHeaderInjector headerInjector) {
            if (headerInjector != null) {
                mHeaderInjector = headerInjector;
            }
            return this;
        }

        /**
         * Builds new instance of {@link HttpProxyCacheServer}.
         *
         * @return proxy cache. Only single instance should be used across whole app.
         */
        public HttpProxyCacheServer build() {
            Config config = buildConfig();
            return new HttpProxyCacheServer(config);
        }

        private Config buildConfig() {
            return new Config(mCacheRoot, mFileNameGenerator, mDiskUsage, mSourceInfoStorage, mHeaderInjector);
        }

    }
}
