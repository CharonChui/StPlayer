package com.st.preload.bean;

/**
 * 主要用于存储http请求源的一些信息，url、数据长度、资源mine
 * 一般情况下，url对应的长度和文件类型是不会变化的，因此将url的length(长度)和mime(文件类型)加入到缓存，
 * 这样就不用每次都打开HttpURLConnection去获取长度和mine了
 * Stores source's info.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class SourceInfo {

    public final String url;
    public final long length;
    public final String mime;

    public SourceInfo(String url, long length, String mime) {
        this.url = url;
        this.length = length;
        this.mime = mime;
    }

    @Override
    public String toString() {
        return "SourceInfo{" +
                "url='" + url + '\'' +
                ", length=" + length +
                ", mime='" + mime + '\'' +
                '}';
    }
}
