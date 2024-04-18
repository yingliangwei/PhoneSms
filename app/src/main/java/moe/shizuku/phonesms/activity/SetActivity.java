package moe.shizuku.phonesms.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.Nullable;

import com.baolian.network.SocketManage;
import com.baolian.network.util.SharedPreferencesManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivitySetBinding;
import moe.shizuku.phonesms.entity.UpdateDialog;
import moe.shizuku.phonesms.entity.UpdateEntity;

/**
 * 设置界面
 */
public class SetActivity extends AppXCompatActivity<ActivitySetBinding> implements OnRecyclerItemClickListener, ActivityResultCallback<ActivityResult> {

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this, SharedPreferencesManager.set);
        initToolbar();
        initRecycler();
    }

    private void initRecycler() {
        List<List<RecyclerEntity>> listList = new ArrayList<>();
        boolean verification = sharedPreferencesManager.getBool("verification", false);

        List<RecyclerEntity> recyclerEntities = new ArrayList<>();
        recyclerEntities.add(new RecyclerEntity(R.mipmap.copy, "验证码复制", "verification", true, verification));
        listList.add(recyclerEntities);

        List<RecyclerEntity> recyclerEntities1 = new ArrayList<>();
        recyclerEntities1.add(new RecyclerEntity(R.mipmap.tuwenbaogao, getString(R.string.Check_for_new_versions), "version"));
        recyclerEntities1.add(new RecyclerEntity(R.mipmap.about, getString(R.string.about), "about"));
        listList.add(recyclerEntities1);

        binding.recycler.add(listList);
        binding.recycler.setOnRecyclerItemClickListener(this);
    }

    private void initToolbar() {
        setNavigationOnClickListener(binding.toolbar);
    }

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        switch (entity.getKey()) {
            case "version":
                SocketManage.request(this);
                break;
            case "about":
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }


    @Override
    public void onItemClick(RecyclerEntity entity, int position, boolean box) {
        if (entity.getKey().equals("verification")) {
            sharedPreferencesManager.savaBool("verification", box);
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
    public void onConnect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            long versionCode = pInfo.getLongVersionCode();
            jsonObject.put("versionCode", versionCode);
            jsonObject.put("package", getPackageName());
            socketManage.send("set", 0, jsonObject);
        } catch (JsonIOException | JSONException | IOException |
                 PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleData(int type, JsonElement data) throws JSONException {
        JSONObject jsonObject1 = new JSONObject(data.toString());
        UpdateEntity entity = UpdateEntity.objectFromData(jsonObject1.toString());
        new UpdateDialog(this, entity);
    }
}