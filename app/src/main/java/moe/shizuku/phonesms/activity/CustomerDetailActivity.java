package moe.shizuku.phonesms.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.baolian.network.SocketManage;
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityCustomerDetailBinding;
import moe.shizuku.phonesms.entity.CustomerEntity;

/**
 * 客服详细
 */
public class CustomerDetailActivity extends AppXCompatActivity<ActivityCustomerDetailBinding> {
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        initToolbar();
        start();
    }

    private void start() {
        SocketManage.request(this);
    }

    private void initToolbar() {
        setNavigationOnClickListener(binding.toolbar);
    }

    @Override
    public void onConnect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            socketManage.send("CustomerDetail", 1, jsonObject);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String msg) {
        super.onSuccess(msg);
    }

    @Override
    public void onError() {
        super.onError();
    }

    @Override
    public void handleMessage(int w, String str) {
        super.handleMessage(w, str);
    }

    @Override
    public void handleData(int type, JsonElement data) throws JSONException {
        JSONObject jsonObject1 = new JSONObject(data.toString());
        CustomerEntity customerEntity = CustomerEntity.objectFromData(jsonObject1.toString());
        binding.name.setText(customerEntity.name);
        Glide.with(this).load(customerEntity.image).into(binding.image);
        binding.wx.setText(customerEntity.wx_name);
        Glide.with(this).load(customerEntity.wx_image).into(binding.wxImage);
        binding.qq.setText(customerEntity.qq_name);
        Glide.with(this).load(customerEntity.qq_image).into(binding.qqImage);
    }
}
