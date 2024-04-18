package moe.shizuku.phonesms.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.baolian.network.SocketManage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xframe.widget.recycler.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.adapter.BuyVipAdapter;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityBuyVipBinding;
import moe.shizuku.phonesms.entity.BuyVipEntity;
import moe.shizuku.phonesms.util.LoginUtil;

/**
 * 购买会员
 */
public class BuyVipActivity extends AppXCompatActivity<ActivityBuyVipBinding> implements RecyclerItemClickListener.OnItemClickListener.Normal {
    private ActivityBuyVipBinding binding;
    private final List<BuyVipEntity> buyVipEntities = new ArrayList<>();
    private BuyVipAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initData();
    }

    private void initData() {
        adapter = new BuyVipAdapter(this, buyVipEntities);
        binding.recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recycler.setAdapter(adapter);
        binding.recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        Message message = new Message();
        message.what = 0;
        SocketManage.request(this, message);
    }

    @Override
    public void onItemClick(View view, int position) {
        BuyVipEntity buyVipEntity = buyVipEntities.get(position);
        String id = buyVipEntity.id;
        Message message = new Message();
        message.what = 1;
        message.obj = id;
        SocketManage.request(this, message);
    }

    @Override
    public void onConnect(SocketManage socketManage, Message message) {
        if (message.what == 0) {
            try {
                JSONObject jsonObject = LoginUtil.toJson(this);
                socketManage.send("buyVip", 0, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message.what == 1) {
            if (message.obj instanceof String) {
                String id = (String) message.obj;
                try {
                    JSONObject jsonObject = LoginUtil.toJson(this);
                    jsonObject.put("buy_id", id);
                    socketManage.send("buyVip", 1, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void handleData(int type, JsonElement data) throws JSONException {
        if (type == 0) {
            if (!data.isJsonArray()) return;
            JSONArray jsonArray = new JSONArray(data.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BuyVipEntity buyVipEntity = new Gson().fromJson(jsonObject.toString(), BuyVipEntity.class);
                buyVipEntities.add(buyVipEntity);
                adapter.notifyItemChanged(buyVipEntities.size());
            }
        } else if (type == 1) {
            if (!data.isJsonObject()) return;
            JSONObject jsonObject = new JSONObject(data.toString());
            String url = jsonObject.getString("url");
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void initView() {
        setNavigationOnClickListener(binding.toolbar);
    }
}
