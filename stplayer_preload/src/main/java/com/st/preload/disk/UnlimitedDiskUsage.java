package com.st.preload.disk;

import java.io.File;

/**
 * Unlimited version of {@link IDiskUsage}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class UnlimitedDiskUsage implements IDiskUsage {

    @Override
    public void touch(File file) {
        // do nothing
    }
}
