package moe.shizuku.phonesms.activity.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.baolian.network.SocketManage;
import com.baolian.network.so.Handler;
import com.baolian.network.so.OnHandler;
import com.baolian.network.util.SharedPreferencesManager;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.activity.AboutActivity;
import moe.shizuku.phonesms.activity.BuyVipActivity;
import moe.shizuku.phonesms.activity.CustomerActivity;
import moe.shizuku.phonesms.activity.FeedbackActivity;
import moe.shizuku.phonesms.activity.SetActivity;
import moe.shizuku.phonesms.adapter.RecyclerAdapter;
import moe.shizuku.phonesms.databinding.PagerMyBinding;
import moe.shizuku.phonesms.util.LoginUtil;

public class MyPager extends RecyclerAdapter<PagerMyBinding> implements SocketManage.OnMessage, OnHandler, Toolbar.OnMenuItemClickListener, OnRecyclerItemClickListener {
    private final Handler handler = new Handler(Looper.myLooper(), this);

    public MyPager(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        initNick();
        initToolbar();
        initRecycler();
        initData();
    }

    private void initToolbar() {
        getBinding().toolbar.setOnMenuItemClickListener(this);
    }

    private void initRecycler() {
        List<List<RecyclerEntity>> listList = new ArrayList<>();

        List<RecyclerEntity> recyclerEntities1 = new ArrayList<>();
        recyclerEntities1.add(new RecyclerEntity(R.mipmap.recharge, getContext().getString(R.string.buy_vip), "recharge_a"));
        recyclerEntities1.add(new RecyclerEntity(R.mipmap.recharge, getContext().getString(R.string.customer), "recharge"));
        listList.add(recyclerEntities1);

        List<RecyclerEntity> recyclerEntities2 = new ArrayList<>();
        recyclerEntities2.add(new RecyclerEntity(R.mipmap.opinion, getContext().getString(R.string.opinion), "Feedback"));
        listList.add(recyclerEntities2);

        getBinding().recycler.add(listList);
        getBinding().recycler.setOnRecyclerItemClickListener(this);
    }

    private void initNick() {
        SocketManage.request(this);
    }

    private void initData() {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getContext(), SharedPreferencesManager.user);
        String id = sharedPreferencesManager.getString("id", null);
        if (id == null) return;
        getBinding().id.setText(id);
        int isVip = sharedPreferencesManager.getInt("isVip", 0);
        if (isVip == 1) {
            String expire = sharedPreferencesManager.getString("expire", "");
            getBinding().c.setText(expire);
        }
        getBinding().vip.setVisibility(isVip == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConnect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = LoginUtil.toJson(getContext());
            socketManage.send("user", 0, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String msg) {
        handler.sendMessage(0, msg);
    }

    @Override
    public void onError() {
        handler.sendMessage(1, getContext().getString(R.string.network_error));
    }

    @Override
    public void handleMessage(int w, String data) {
        if (w == 0) {
            try {
                handleJson(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (w == 1) {
            Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleJson(String data) throws JSONException {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getContext(), SharedPreferencesManager.user);
        JSONObject jsonObject = new JSONObject(data);
        int code = jsonObject.getInt("code");
        if (code == 2) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            sharedPreferencesManager.remove("key");
            sharedPreferencesManager.remove("id");
        } else if (code == 0) {
            JSONObject data1 = jsonObject.getJSONObject("data");
            int isVip = data1.getInt("isVip");
            if (isVip == 1) {
                String expire = data1.getString("expire");
                getBinding().c.setText(expire);
                sharedPreferencesManager.saveString("expire", expire);
            }
            getBinding().vip.setVisibility(isVip == 0 ? View.GONE : View.VISIBLE);
            sharedPreferencesManager.saveInt("isVip", isVip);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        getContext().startActivity(new Intent(getContext(), SetActivity.class));
        return false;
    }

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        switch (entity.getKey()) {
            case "Feedback":
                getContext().startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
            case "recharge":
                getContext().startActivity(new Intent(getContext(), CustomerActivity.class));
                break;
            case "recharge_a":
                getContext().startActivity(new Intent(getContext(), BuyVipActivity.class));
                break;
        }
    }
}
