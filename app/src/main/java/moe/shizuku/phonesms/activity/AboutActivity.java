package moe.shizuku.phonesms.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xframe.widget.entity.RecyclerEntity;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityAboutBinding;

/**
 * 关于
 */
public class AboutActivity extends AppXCompatActivity<ActivityAboutBinding> {
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initRecycler();
    }

    private void initRecycler() {
        List<RecyclerEntity> recyclerEntities = new ArrayList<>();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            recyclerEntities.add(new RecyclerEntity(R.mipmap.about, getString(R.string.version_code), String.valueOf(pInfo.getLongVersionCode()), "", false));
            recyclerEntities.add(new RecyclerEntity(R.mipmap.about, getString(R.string.version), pInfo.versionName, "", false));
        } catch (Exception ignored) {
        }
        entity.add(recyclerEntities);
        binding.recycle.add(entity);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }
}
