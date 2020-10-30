package com.st.preload.headers;

import java.util.HashMap;
import java.util.Map;

/**
 * Empty {@link IHeaderInjector} implementation.
 *
 * @author Lucas Nelaupe (https://github.com/lucas34).
 */
public class EmptyHeadersInjector implements IHeaderInjector {

    @Override
    public Map<String, String> addHeaders(String url) {
        return new HashMap<>();
    }

}
