package com.st.preload.source;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({UrlSourceType.SOURCE_TYPE_URLCONNECTION, UrlSourceType.SOURCE_TYPE_OKHTTP})
@Retention(RetentionPolicy.SOURCE)
public @interface UrlSourceType {

    int SOURCE_TYPE_URLCONNECTION = 0;
    int SOURCE_TYPE_OKHTTP = 1;
}
