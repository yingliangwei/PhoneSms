package moe.shizuku.phonesms.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewbinding.ViewBinding;

import com.baolian.network.SocketManage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.entity.MessageEntity;
import moe.shizuku.phonesms.util.Handler;
import moe.shizuku.phonesms.util.OnHandler;
import moe.shizuku.phonesms.util.ViewBindingUtil;


public class AppXCompatActivity<T extends ViewBinding> extends AppCompatActivity implements ActivityResultCallback<ActivityResult> , OnHandler, SocketManage.OnMessage{
    public T binding;
    public final Handler handler = new Handler(Looper.myLooper(), this);
    public final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarColor(Color.WHITE, true);
        binding = ViewBindingUtil.inflate(getClass(), getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findToolbar(binding.getRoot());
        if (toolbar == null) return;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 递归遍历 View 树查找 Toolbar
     *
     * @param view 要查找的 View
     * @return 查找到的 Toolbar，如果没有找到则返回 null
     */
    private Toolbar findToolbar(View view) {
        // 如果传入的 View 为空，则直接返回 null
        if (view == null) {
            return null;
        }
        // 如果传入的 View 是 Toolbar，则直接返回
        if (view instanceof Toolbar) {
            return (Toolbar) view;
        }
        // 如果传入的 View 是 ViewGroup，则遍历它的子 View
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                Toolbar toolbar = findToolbar(childView); // 递归查找子 View
                if (toolbar != null) {
                    return toolbar; // 如果找到了 Toolbar，直接返回
                }
            }
        }
        // 如果遍历完所有子 View 都没有找到 Toolbar，则返回 null
        return null;
    }


    public void startActivityForResult(Intent intent) {
        launcher.launch(intent);
    }

    public void setBarColor(int color, boolean is) {
        Window window = getWindow();
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
        window.setNavigationBarDividerColor(color);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), window.getDecorView());
        controller.setAppearanceLightStatusBars(is);
    }

    @Override
    public void onActivityResult(ActivityResult o) {

    }

    public void setNavigationOnClickListener(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            try {
                if (!handleJson(str)) {
                    handleError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean handleJson(String msg) throws JSONException {
        MessageEntity message = new Gson().fromJson(msg, MessageEntity.class);
        if (message == null) {
            Toast.makeText(this, R.string.handleExceotion, Toast.LENGTH_SHORT).show();
            return false;
        } else if (message.code != 0) {
            Toast.makeText(this, message.msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        handleData(message.type, message.data);
        return true;
    }

    public void handleData(int type, JsonElement data) throws JSONException {
    }

    public void handleError() {
    }


    @Override
    public void onSuccess(String msg) {
        handler.sendMessage(1, msg);
    }

    @Override
    public void onError() {
        handler.sendMessage(0, getString(R.string.network_error));
    }

    public Handler getHandler() {
        return handler;
    }
}
