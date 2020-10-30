package com.st.preload.ping;

import android.util.Log;

import androidx.annotation.NonNull;

import com.st.preload.exception.ProxyCacheException;
import com.st.preload.source.HttpUrlSource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Pings {@link com.st.preload.server.HttpProxyCacheServer} to make sure it works.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class Pinger {
    private static final String LOG_TAG = Pinger.class.getSimpleName();
    private static final String PING_REQUEST = "ping";
    private static final String PING_RESPONSE = "ping ok";

    private final ExecutorService pingExecutor = Executors.newSingleThreadExecutor();
    private final String host;
    private final int port;

    public Pinger(@NonNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean ping(int maxAttempts, int startTimeout) {
        if (maxAttempts < 1) {
            return false;
        }
        if (startTimeout <= 0) {
            return false;
        }

        int timeout = startTimeout;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                Future<Boolean> pingFuture = pingExecutor.submit(new PingCallable());
                boolean pinged = pingFuture.get(timeout, MILLISECONDS);
                if (pinged) {
                    return true;
                }
            } catch (TimeoutException e) {
                Log.e(LOG_TAG, "ping time out : " + e.toString());
            } catch (InterruptedException | ExecutionException e) {
                Log.e(LOG_TAG, "ping time out : " + e.toString());
            }
            attempts++;
            timeout *= 2;
        }
        String error = String.format(Locale.US, "Error pinging server (attempts: %d, max timeout: %d). " +
                        "If you see this message, please, report at https://github.com/danikula/AndroidVideoCache/issues/134. " +
                        "Default proxies are: %s"
                , attempts, timeout / 2, getDefaultProxies());
        return false;
    }

    private List<Proxy> getDefaultProxies() {
        try {
            ProxySelector defaultProxySelector = ProxySelector.getDefault();
            return defaultProxySelector.select(new URI(getPingUrl()));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isPingRequest(String request) {
        return PING_REQUEST.equals(request);
    }

    public void responseToPing(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write("HTTP/1.1 200 OK\n\n".getBytes());
        out.write(PING_RESPONSE.getBytes());
    }

    private boolean pingServer() throws ProxyCacheException {
        String pingUrl = getPingUrl();
        HttpUrlSource source = new HttpUrlSource(pingUrl);
        try {
            byte[] expectedResponse = PING_RESPONSE.getBytes();
            source.open(0);
            byte[] response = new byte[expectedResponse.length];
            source.read(response);
            return Arrays.equals(expectedResponse, response);
        } catch (ProxyCacheException e) {
            return false;
        } finally {
            source.close();
        }
    }

    private String getPingUrl() {
        return String.format(Locale.US, "http://%s:%d/%s", host, port, PING_REQUEST);
    }

    private class PingCallable implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return pingServer();
        }
    }

}
