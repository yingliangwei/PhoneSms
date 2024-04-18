package moe.shizuku.phonesms.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baolian.network.SocketManage;
import com.baolian.network.util.SharedPreferencesManager;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityLoginBinding;
import moe.shizuku.phonesms.util.EditUtil;
import moe.shizuku.phonesms.util.OnHandler;

/**
 * 登录界面
 */
public class LoginActivity extends AppXCompatActivity<ActivityLoginBinding> implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding.login.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        EditUtil.PlainText(binding.togglePwd, binding.pass);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.register.getId()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (v.getId() == binding.login.getId()) {
            login();
        }
    }

    private void login() {
        String mail = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        if (mail.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, R.string.Inspection_content, Toast.LENGTH_SHORT).show();
            return;
        }
        SocketManage.request(this);
    }

    @Override
    public void handleMessage(int w, String str) {
        super.handleMessage(w, str);
    }

    @Override
    public void onSuccess(String data) {
        super.onSuccess(data);
    }

    @Override
    public void onError() {
        super.onError();
    }

    @Override
    public void onConnect(SocketManage socketManage) {
        String mail = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        try {
            JSONObject data = new JSONObject();
            data.put("mail", mail);
            data.put("pass", pass);
            data.put("manufacturer", Build.MANUFACTURER);
            socketManage.send("LoginActivity", 0, data);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleData(int type, JsonElement data) throws JSONException {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this, SharedPreferencesManager.user);
        JSONObject dataJson = new JSONObject(data.toString());
        String id = dataJson.getString("id");
        String key = dataJson.getString("key");
        sharedPreferencesManager.saveString("id", id);
        sharedPreferencesManager.saveString("key", key);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
