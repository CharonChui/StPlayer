package com.st.preload.disk;

import java.io.File;
import java.io.IOException;

public interface IDiskUsage {
    void touch(File file) throws IOException;
}
