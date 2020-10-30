package com.st.preload.source;

import android.text.TextUtils;
import android.util.Log;

import com.st.preload.exception.InterruptedProxyCacheException;
import com.st.preload.exception.ProxyCacheException;
import com.st.preload.utils.Preconditions;
import com.st.preload.utils.ProxyCacheUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.st.preload.utils.ProxyCacheUtils.DEFAULT_BUFFER_SIZE;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;

public class OkHttpSource implements ISource {
    private static final String LOG_TAG = OkHttpSource.class.getSimpleName();
    private static final int MAX_REDIRECTS = 5;
    //
    private final OkHttpClient httpClient = new OkHttpClient();
    private InputStream inputStream;
    protected String url;
    protected volatile long length = Integer.MIN_VALUE;
    protected volatile String mime;

    public OkHttpSource(String url) {
        this(url, ProxyCacheUtils.getSupposablyMime(url));
    }

    public OkHttpSource(OkHttpSource source) {
        this.url = source.url;
        this.mime = source.mime;
        this.length = source.length;
    }

    public OkHttpSource(String url, String mime) {
        this.url = Preconditions.checkNotNull(url);
        this.mime = mime;
    }

    @Override
    public long length() throws ProxyCacheException {
        if (length == Integer.MIN_VALUE) {
            fetchContentInfo();
        }
        return length;
    }

    public synchronized String getMime() throws ProxyCacheException {
        if (TextUtils.isEmpty(mime)) {
            fetchContentInfo();
        }
        return mime;
    }

    @Override
    public void open(long offset) throws ProxyCacheException {
        try {
            Response response = openConnection(offset, -1);
            mime = response.body().contentType().toString();
            length = readSourceAvailableBytes(response, offset);
            inputStream = new BufferedInputStream(response.body().byteStream(), DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening connection for " + url + " with offset " + offset, e);
        }
    }

    private long readSourceAvailableBytes(Response response, long offset) {
        int responseCode = response.code();
        int contentLength = (int) response.body().contentLength();
        return responseCode == HTTP_OK ? contentLength
                : responseCode == HTTP_PARTIAL ? contentLength + offset : length;
    }

    @Override
    public int read(byte[] buffer) throws ProxyCacheException {
        if (inputStream == null) {
            throw new ProxyCacheException("Error reading data from " + url + ": connection is absent!");
        }
        try {
            return inputStream.read(buffer, 0, buffer.length);
        } catch (InterruptedIOException e) {
            throw new InterruptedProxyCacheException("Reading source " + url + " is interrupted", e);
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading data from " + url, e);
        }
    }

    @Override
    public void close() {
        ProxyCacheUtils.close(inputStream);
    }

    private void fetchContentInfo() throws ProxyCacheException {
//        LogUtil.d(LOG_TAG, "Read content info from " + url);
        Response response;
        try {
            response = openConnectionForHeader(10000);
            if (response == null || !response.isSuccessful()) {
                throw new ProxyCacheException("Fail to fetchContentInfo: " + url);
            }
            length = (int) response.body().contentLength();
            mime = response.body().contentType().toString();
//            LogUtil.i(LOG_TAG, "Content info for `" + url + "`: mime: " + mime + ", content-length: " + length);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching info from " + url, e);
        } finally {
//            LogUtil.d(LOG_TAG, "Closed connection from :" + url);
        }
    }

    private Response openConnectionForHeader(int timeout) throws IOException, ProxyCacheException {
//        if (timeout > 0) {
//            httpClient.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
//            httpClient.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
//            httpClient.setWriteTimeout(timeout, TimeUnit.MILLISECONDS);
//        }
        Response response;
        boolean isRedirect = false;
        String newUrl = this.url;
        int redirectCount = 0;
        do {
            Request request = new Request.Builder()
                    .head()
                    .url(newUrl)
                    .build();
            response = httpClient.newCall(request).execute();
            if (response.isRedirect()) {
                newUrl = response.header("Location");
                isRedirect = response.isRedirect();
                redirectCount++;
            }
            if (redirectCount > MAX_REDIRECTS) {
                throw new ProxyCacheException("Too many redirects: " + redirectCount);
            }
        } while (isRedirect);

        return response;
    }

    private Response openConnection(long offset, int timeout) throws IOException, ProxyCacheException {
        if (timeout > 0) {
//            httpClient.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
//            httpClient.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
//            httpClient.setWriteTimeout(timeout, TimeUnit.MILLISECONDS);
        }
        Response response;
        boolean isRedirect = false;
        String newUrl = this.url;
        int redirectCount = 0;
        do {
//            LogUtil.d(LOG_TAG, "Open connection" + (offset > 0 ? " with offset " + offset : "") + " to " + url);
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.get();
            requestBuilder.url(newUrl);
            if (offset > 0) {
                requestBuilder.addHeader("Range", "bytes=" + offset + "-");
            }
            response = httpClient.newCall(requestBuilder.build()).execute();
            if (response.isRedirect()) {
                newUrl = response.header("Location");
                isRedirect = response.isRedirect();
                redirectCount++;
            }
            if (redirectCount > MAX_REDIRECTS) {
                throw new ProxyCacheException("Too many redirects: " + redirectCount);
            }
        } while (isRedirect);

        return response;
    }

    public String getUrl() {
        return url;
    }
}
