package moe.shizuku.phonesms.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class UpdateEntity {
    @SerializedName("versionCode")
    private String versionCode;
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("Introduction")
    private String Introduction;
    @SerializedName("download")
    private String download;
    @SerializedName("time")
    private String time;

    public static UpdateEntity objectFromData(String str) {
        return new Gson().fromJson(str, UpdateEntity.class);
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
