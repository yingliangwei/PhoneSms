package moe.shizuku.phonesms.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baolian.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityRegisterBinding;
import moe.shizuku.phonesms.util.EditUtil;

/**
 * 注册
 */
public class RegisterActivity extends AppXCompatActivity<ActivityRegisterBinding> implements View.OnClickListener {
    protected ActivityRegisterBinding binding;
    private final CountDownTimer downTimer = new CountDownTimer(50_000, 1000) {
        @SuppressLint("StringFormatMatches")
        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = millisUntilFinished / 1000;
            binding.getVerify.setText(String.format(getString(R.string.residue), seconds));
        }

        @Override
        public void onFinish() {
            binding.getVerify.setText(R.string.app_get_verify);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.getVerify.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        EditUtil.PlainText(binding.togglePwd, binding.pass);
        EditUtil.PlainText(binding.togglePwd1, binding.pass1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downTimer.cancel();
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
            String verify = binding.verify.getText().toString();
            String pass = binding.pass.getText().toString();
            String mail = binding.mail.getText().toString();
            JSONObject data = new JSONObject();
            data.put("mail", mail);
            data.put("verify", verify);
            data.put("pass", pass);
            socketManage.send("RegisterActivity", 1, data);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleJson(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        int code = jsonObject.getInt("code");
        int type = jsonObject.getInt("type");
        if (code == 0) {
            if (type == 0) {
                downTimer.start();
            } else if (type == 1) {
                finish();
            }
        } else if (code == 1) {
            if (type == 1) {
                binding.getVerify.setText(R.string.app_get_verify);
            }
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.getVerify.getId()) {
            if (binding.getVerify.getText().toString().equals(getString(R.string.app_get_verify))) {
                getVerify();
            }
        } else if (v.getId() == binding.register.getId()) {
            register();
        }
    }

    private void register() {
        String verify = binding.verify.getText().toString();
        String pass = binding.pass.getText().toString();
        String pass1 = binding.pass1.getText().toString();
        String mail = binding.mail.getText().toString();
        if (verify.isEmpty()) {
            Toast.makeText(this, R.string.verify_no_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mail.contains("@")) {
            Toast.makeText(this, R.string.Incorrect_email_address, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass1.equals(pass)) {
            Toast.makeText(this, R.string.passwords_different, Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 8 || pass.length() > 26) {
            Toast.makeText(this, R.string.between_8_26_digits, Toast.LENGTH_SHORT).show();
            return;
        }
        SocketManage.request(this);
    }

    private void getVerify() {
        String mail = binding.mail.getText().toString();
        if (!mail.contains("@")) {
            Toast.makeText(this, R.string.Incorrect_email_address, Toast.LENGTH_SHORT).show();
            return;
        }
        SocketManage.request(new SocketManage.OnMessage() {
            @Override
            public void onConnect(SocketManage socketManage) {
                try {
                    String mail = binding.mail.getText().toString();
                    JSONObject data = new JSONObject();
                    data.put("mail", mail);
                    socketManage.send("RegisterActivity", 0, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                RegisterActivity.super.onError();
            }

            @Override
            public void onSuccess(String msg) {
                RegisterActivity.super.onSuccess(msg);
            }
        });
    }
}
