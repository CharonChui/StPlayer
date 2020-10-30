package com.st.preload.config;

import com.st.preload.disk.IDiskUsage;
import com.st.preload.filename.IFileNameGenerator;
import com.st.preload.headers.IHeaderInjector;
import com.st.preload.sourcestorage.SourceInfoStorage;

import java.io.File;

public class Config {
    // 缓存文件的目录
    private final File mCacheRoot;
    // 缓存文件命名生成器
    private final IFileNameGenerator mFileNameGenerator;
    // 磁盘使用情况统计
    private final IDiskUsage mDiskUsage;
    // 存储资源的相关信息
    private final SourceInfoStorage mSourceInfoStorage;
    // 网络请求，可以插入header
    private final IHeaderInjector mHeaderInjector;

    public Config(File cacheRoot, IFileNameGenerator fileNameGenerator, IDiskUsage diskUsage,
                  SourceInfoStorage sourceInfoStorage, IHeaderInjector headerInjector) {
        mCacheRoot = cacheRoot;
        mFileNameGenerator = fileNameGenerator;
        mDiskUsage = diskUsage;
        mSourceInfoStorage = sourceInfoStorage;
        mHeaderInjector = headerInjector;
    }

    public File getCacheRoot() {
        return mCacheRoot;
    }

    public IFileNameGenerator getFileNameGenerator() {
        return mFileNameGenerator;
    }

    public IDiskUsage getDiskUsage() {
        return mDiskUsage;
    }

    public SourceInfoStorage getSourceInfoStorage() {
        return mSourceInfoStorage;
    }

    public IHeaderInjector getHeaderInjector() {
        return mHeaderInjector;
    }

    public File generateCacheFile(String url) {
        String name = mFileNameGenerator.generate(url);
        return new File(mCacheRoot, name);
    }
}
