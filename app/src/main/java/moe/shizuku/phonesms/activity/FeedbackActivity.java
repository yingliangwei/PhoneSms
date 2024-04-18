package moe.shizuku.phonesms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baolian.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityFeedbackBinding;
import moe.shizuku.phonesms.util.LoginUtil;

/**
 * 建议反馈
 */
public class FeedbackActivity extends AppXCompatActivity<ActivityFeedbackBinding> implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initData();
    }

    private void initData() {
        binding.post.setOnClickListener(this);
    }

    private void initToolbar() {
        setNavigationOnClickListener(binding.toolbar);
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
    public void onConnect(SocketManage socketManage) {
        try {
            String contact = binding.contact.getText().toString();
            String message = binding.message.getText().toString();
            JSONObject jsonObject = LoginUtil.toJson(this);
            jsonObject.put("contact", contact);
            jsonObject.put("message", message);
            socketManage.send("Feedback", 0, jsonObject);
        } catch (JSONException | IOException e) {
           e.printStackTrace();
        }
    }

    @Override
    public boolean handleJson(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        int code = jsonObject.getInt("code");
        if (code != 0) return false;
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.post.getId()) {
            String contact = binding.contact.getText().toString();
            String message = binding.message.getText().toString();
            if (contact.isEmpty() && message.isEmpty()) {
                Toast.makeText(this, R.string.Input_is_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.request(this);
        }
    }
}
