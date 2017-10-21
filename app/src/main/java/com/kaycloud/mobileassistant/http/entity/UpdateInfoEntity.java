package com.kaycloud.mobileassistant.http.entity;

/**
 * @author: kaycloud
 * @date: 2017/10/20.
 */

public class UpdateInfoEntity {

    /**
     * versionCode : 2
     * versionName : 1.1
     * Desc : 哇哈哈哈哈哈哈哈哈哈,你妈就大幅度
     * url : http://192.168.123.104:8080/appv2.apk
     */

    private int versionCode;
    private String versionName;
    private String Desc;
    private String url;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
