package moe.shizuku.phonesms.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CustomerEntity {
    @SerializedName("name")
    public String name;
    @SerializedName("image")
    public String image;
    @SerializedName("id")
    public String id;
    @SerializedName("wx_name")
    public String wx_name;
    @SerializedName("wx_image")
    public String wx_image;
    @SerializedName("qq_name")
    public String qq_name;
    @SerializedName("qq_image")
    public String qq_image;

    public static CustomerEntity objectFromData(String str) {

        return new Gson().fromJson(str, CustomerEntity.class);
    }
}
