package com.st.preload;

import androidx.annotation.NonNull;

import com.st.preload.utils.Preconditions;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * {@link ProxySelector} that ignore system default proxies for concrete host.
 * <p>
 * It is important to <a href="https://github.com/danikula/AndroidVideoCache/issues/28">ignore system proxy</a> for localhost connection.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class IgnoreHostProxySelector extends ProxySelector {

    private static final List<Proxy> NO_PROXY_LIST = Arrays.asList(Proxy.NO_PROXY);

    private final ProxySelector defaultProxySelector;
    private final String hostToIgnore;
    private final int portToIgnore;

    IgnoreHostProxySelector(@NonNull ProxySelector defaultProxySelector, @NonNull String hostToIgnore, int portToIgnore) {
        this.defaultProxySelector = Preconditions.checkNotNull(defaultProxySelector);
        this.hostToIgnore = Preconditions.checkNotNull(hostToIgnore);
        this.portToIgnore = portToIgnore;
    }

    public static void install(String hostToIgnore, int portToIgnore) {
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        ProxySelector ignoreHostProxySelector = new IgnoreHostProxySelector(defaultProxySelector, hostToIgnore, portToIgnore);
        ProxySelector.setDefault(ignoreHostProxySelector);
    }

    @Override
    public List<Proxy> select(URI uri) {
        boolean ignored = hostToIgnore.equals(uri.getHost()) && portToIgnore == uri.getPort();
        return ignored ? NO_PROXY_LIST : defaultProxySelector.select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
        defaultProxySelector.connectFailed(uri, address, failure);
    }
}
