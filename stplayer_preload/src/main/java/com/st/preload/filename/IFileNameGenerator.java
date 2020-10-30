package com.st.preload.filename;

/**
 * 根据要缓冲的视频url生成对应的缓冲文件名
 */
public interface IFileNameGenerator {
    /**
     * 根据视频url生成缓冲文件名
     *
     * @param url 要缓冲的视频url
     * @return 对应该视频的文件名
     */
    String generate(String url);
}
