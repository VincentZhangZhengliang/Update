package com.vincent.update;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vincent on 2017/3/30 12:53.
 */

public class Version {
    /**
     * versioncode : 2
     * desc : 有新的功能
     * url : http://192.168.1.103:8080/safeguard/SafeGuard.apk
     */

    @SerializedName("versioncode")
    public int versioncode;
    @SerializedName("desc")
    public String desc;
    @SerializedName("url")
    public String url;
}
