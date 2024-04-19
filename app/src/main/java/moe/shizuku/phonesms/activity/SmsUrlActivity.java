package moe.shizuku.phonesms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baolian.network.util.SharedPreferencesManager;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivitySmsUrlBinding;

public class SmsUrlActivity extends AppXCompatActivity<ActivitySmsUrlBinding> implements View.OnClickListener {
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this, SharedPreferencesManager.set);
        initView();
        initData();
    }

    private void initData() {
        String url = sharedPreferencesManager.getString("url", "");
        binding.userId.setText(url);
    }

    private void initView() {
        binding.ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.ok.getId()) {
            sharedPreferencesManager.saveString("url", binding.userId.getText().toString());
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
        }
    }
}