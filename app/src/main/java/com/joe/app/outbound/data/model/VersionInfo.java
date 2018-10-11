package com.joe.app.outbound.data.model;

import java.io.Serializable;

/**
 * 版本信息对象
 */
public class VersionInfo implements Serializable {


    public Data result;

    public class Data implements Serializable {

        public String versionCode;
        public String version;
        public String downloadUrl;
        public String md5;
        public String versionDesc;
    }
}
