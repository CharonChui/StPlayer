package com.st.preload.server;

import com.st.preload.GetRequest;
import com.st.preload.cache.CacheListener;
import com.st.preload.exception.ProxyCacheException;

import java.io.IOException;
import java.net.Socket;

public interface IProxyCache {
    void registerCacheListener(CacheListener cacheListener);

    void processRequest(GetRequest request, Socket socket) throws IOException, ProxyCacheException;

    void shutdown();
}
