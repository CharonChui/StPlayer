package com.st.preload.server;


import android.util.Log;

import com.st.preload.GetRequest;
import com.st.preload.cache.CacheListener;
import com.st.preload.cache.ICache;
import com.st.preload.exception.InterruptedProxyCacheException;
import com.st.preload.exception.ProxyCacheException;
import com.st.preload.source.ISource;
import com.st.preload.utils.Preconditions;
import com.st.preload.utils.ProxyCacheUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Proxy for {@link ISource} with caching support ({@link ICache}).
 * <p/>
 * Can be used only for sources with persistent data (that doesn't change with time).
 * Method {@link #read(byte[], long, int, long)} will be blocked while fetching data from source.
 * Useful for streaming something with caching e.g. streaming video/audio etc.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public abstract class ProxyCache implements IProxyCache {
    protected static final String LOG_TAG = ProxyCache.class.getName();
    private static final int MAX_READ_SOURCE_ATTEMPTS = 1;

    private final ISource source;
    private final ICache cache;
    private final Object wc = new Object();
    private final Object stopLock = new Object();
    private final AtomicInteger readSourceErrorsCount;
    private volatile Thread sourceReaderThread;
    private volatile boolean stopped;
    private volatile int percentsAvailable = -1;

    public ProxyCache(ISource source, ICache cache) {
        this.source = Preconditions.checkNotNull(source);
        this.cache = Preconditions.checkNotNull(cache);
        this.readSourceErrorsCount = new AtomicInteger();
    }

    public int read(byte[] buffer, long offset, int length, long prefetchSize) throws ProxyCacheException {
        ProxyCacheUtils.assertBuffer(buffer, offset, length);

        while (!cache.isCompleted() && cache.available() < (offset + length) && !stopped) {
            // 判断缓存中是否存在，如果不存在则去进行网络连接，获取云端数据并写入本地缓存
            readSourceAsync(prefetchSize);
            // 等待一秒钟
            waitForSourceData();
            // 监测读取失败的次数
            checkReadSourceErrorsCount();
        }
        // 往文件中存数据
        int read = cache.read(buffer, offset, length);
        if (cache.isCompleted() && percentsAvailable != 100) {
            percentsAvailable = 100;
            // 回调通知缓存进度
            onCachePercentsAvailableChanged(100);
        }
        return read;
    }

    private void checkReadSourceErrorsCount() throws ProxyCacheException {
        int errorsCount = readSourceErrorsCount.get();
        if (errorsCount >= MAX_READ_SOURCE_ATTEMPTS) {
            readSourceErrorsCount.set(0);
            throw new ProxyCacheException("Error reading source " + errorsCount + " times");
        }
    }

    @Override
    public abstract void registerCacheListener(CacheListener cacheListener);

    @Override
    public abstract void processRequest(GetRequest request, Socket socket) throws IOException, ProxyCacheException;

    @Override
    public void shutdown() {
        synchronized (stopLock) {
            try {
                stopped = true;
                if (sourceReaderThread != null) {
                    sourceReaderThread.interrupt();
                }
                cache.close();
            } catch (ProxyCacheException e) {
                onError(e);
            }
        }
    }

    private synchronized void readSourceAsync(long prefetchSize) {
        boolean readingInProgress = sourceReaderThread != null && sourceReaderThread.getState() != Thread.State.TERMINATED;
        if (!stopped && !cache.isCompleted() && !readingInProgress) {
            // 开启一个异步线程去读取网络资源
            sourceReaderThread = new Thread(new SourceReaderRunnable(prefetchSize), "Source reader for " + source);
            sourceReaderThread.start();
        }
    }

    private void waitForSourceData() throws ProxyCacheException {
        synchronized (wc) {
            try {
                wc.wait(1000);
            } catch (InterruptedException e) {
                throw new ProxyCacheException("Waiting source data is interrupted!", e);
            }
        }
    }

    private void notifyNewCacheDataAvailable(long cacheAvailable, long sourceAvailable) {
        onCacheAvailable(cacheAvailable, sourceAvailable);

        synchronized (wc) {
            wc.notifyAll();
        }
    }

    protected void onCacheAvailable(long cacheAvailable, long sourceLength) {
        boolean zeroLengthSource = sourceLength == 0;
        int percents = zeroLengthSource ? 100 : (int) ((float) cacheAvailable / sourceLength * 100);
        boolean percentsChanged = percents != percentsAvailable;
        boolean sourceLengthKnown = sourceLength >= 0;
        if (sourceLengthKnown && percentsChanged) {
            onCachePercentsAvailableChanged(percents);
        }
        percentsAvailable = percents;
    }

    protected void onCachePercentsAvailableChanged(int percentsAvailable) {
    }

    private void readSource(long prefetchSize) {
        long sourceAvailable = -1;
        long offset = 0;
        try {
            offset = cache.available();
            // 打开httpurlconnection连接
            source.open(offset);
            sourceAvailable = source.length();
            byte[] buffer = new byte[ProxyCacheUtils.DEFAULT_BUFFER_SIZE];
            int readBytes;
            // 不断读取网络数据
            while ((readBytes = source.read(buffer)) != -1) {
                synchronized (stopLock) {
                    if (isStopped()) {
                        return;
                    }
                    // 将数据存到本地文件中
                    cache.append(buffer, readBytes);
                }
                offset += readBytes;
                Log.d(LOG_TAG, "proxy read source : " + offset);
                notifyNewCacheDataAvailable(offset, sourceAvailable);
                if (prefetchSize >= ProxyCacheUtils.DEFAULT_BUFFER_SIZE && offset >= prefetchSize) {
                    Log.e(LOG_TAG, "cancel :" + offset);
                    closeSource();
                }
            }
            tryComplete();
            onSourceRead();
        } catch (Throwable e) {
            readSourceErrorsCount.incrementAndGet();
            onError(e);
        } finally {
            closeSource();
            notifyNewCacheDataAvailable(offset, sourceAvailable);
        }
    }

    private void onSourceRead() {
        // guaranteed notify listeners after source read and cache completed
        percentsAvailable = 100;
        onCachePercentsAvailableChanged(percentsAvailable);
    }

    private void tryComplete() throws ProxyCacheException {
        synchronized (stopLock) {
            if (!isStopped() && cache.available() == source.length()) {
                cache.complete();
            }
        }
    }

    private boolean isStopped() {
        return Thread.currentThread().isInterrupted() || stopped;
    }

    private void closeSource() {
        try {
            source.close();
        } catch (ProxyCacheException e) {
            onError(new ProxyCacheException("Error closing source " + source, e));
        }
    }

    protected final void onError(final Throwable e) {
        boolean interruption = e instanceof InterruptedProxyCacheException;
        if (interruption) {
            Log.d(LOG_TAG, "ProxyCache is interrupted");
        } else {
            Log.d(LOG_TAG, "ProxyCache error" + e);
        }
    }

    private class SourceReaderRunnable implements Runnable {
        private final long prefetchSize;

        public SourceReaderRunnable() {
            this(0);
        }

        public SourceReaderRunnable(long prefetchSize) {
            this.prefetchSize = prefetchSize;
        }

        @Override
        public void run() {
            readSource(prefetchSize);
        }
    }
}
