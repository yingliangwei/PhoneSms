package moe.shizuku.phonesms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.baolian.network.SocketManage;
import com.google.gson.JsonElement;
import com.xframe.widget.recycler.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.adapter.CustomerAdapter;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityCustomerBinding;
import moe.shizuku.phonesms.entity.CustomerEntity;


/**
 * 客服
 */
public class CustomerActivity extends AppXCompatActivity<ActivityCustomerBinding> implements RecyclerItemClickListener.OnItemClickListener.Normal {
    private ActivityCustomerBinding binding;
    private final List<CustomerEntity> entities = new ArrayList<>();
    private CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        start();
    }

    private void initRecycler() {
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter(this, entities);
        binding.recycle.setAdapter(customerAdapter);
        binding.recycle.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    @Override
    public void onItemClick(View view, int position) {
        CustomerEntity entity = entities.get(position);
        Intent intent = new Intent(view.getContext(), CustomerDetailActivity.class);
        intent.putExtra("id", entity.id);
        startActivity(intent);
    }

    private void start() {
        SocketManage.request(this);
    }

    private void initToolbar() {
        setNavigationOnClickListener(binding.toolbar);
    }

    @Override
    public void onConnect(SocketManage socketManage) {
        try {
            socketManage.send("Customer", 0, new JSONObject());
        } catch (IOException | JSONException e) {
           e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String data) {
        super.handleMessage(w, data);
    }

    @Override
    public void handleData(int type, JsonElement data) throws JSONException {
        if (!data.isJsonArray()) return;
        JSONArray jsonObject1 = new JSONArray(data.toString());
        for (int i = 0; i < jsonObject1.length(); i++) {
            JSONObject jsonObject2 = jsonObject1.getJSONObject(i);
            CustomerEntity customerEntity = CustomerEntity.objectFromData(jsonObject2.toString());
            entities.add(customerEntity);
            customerAdapter.notifyItemChanged(entities.size());
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
}
