package moe.shizuku.phonesms.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class MessageEntity {
    @SerializedName("msg")
    public String msg;
    @SerializedName("code")
    public int code;
    @SerializedName("type")
    public int type;
    @SerializedName("data")
    public JsonElement data;
}
