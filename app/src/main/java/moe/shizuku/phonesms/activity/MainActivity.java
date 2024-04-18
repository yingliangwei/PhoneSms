package moe.shizuku.phonesms.activity;

import static android.provider.Telephony.Sms.getDefaultSmsPackage;

import android.app.role.RoleManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.baolian.network.util.SharedPreferencesManager;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.activity.pager.MyPager;
import moe.shizuku.phonesms.activity.pager.SmsPager;
import moe.shizuku.phonesms.adapter.PagerAdapter;
import moe.shizuku.phonesms.adapter.RecyclerAdapter;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityMainBinding;

public class MainActivity extends AppXCompatActivity<ActivityMainBinding> implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycler();
        initBottom();
        initPermission();
    }

    private void initPermission() {
        if (getDefaultSmsPackage(this) != null && !getDefaultSmsPackage(this).equals(this.getPackageName())) {
            RoleManager roleManager = this.getSystemService(RoleManager.class);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    Log.d("role", "role");
                } else {
                    Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                    startActivityForResult(roleRequestIntent);
                }
            }
        }
    }

    private void initBottom() {
        binding.bottom.setOnItemSelectedListener(this);
    }

    private void initRecycler() {
        List<RecyclerAdapter<?>> recyclerAdapters = new ArrayList<>();
        recyclerAdapters.add(new SmsPager(this));
        recyclerAdapters.add(new MyPager(this));
        binding.pager.setAdapter(new PagerAdapter(recyclerAdapters));
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                isLogin(position);
                binding.bottom.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private void isLogin(int order) {
        if (order == 1 || order == 2) {
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this, SharedPreferencesManager.user);
            String _key = sharedPreferencesManager.getString("key", null);
            if (_key == null) {
                binding.bottom.getMenu().getItem(0).setChecked(true);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                binding.pager.setCurrentItem(order);
            }
        } else {
            binding.pager.setCurrentItem(order);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        binding.pager.setCurrentItem(menuItem.getOrder());
        return false;
    }
}
