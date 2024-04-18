package moe.shizuku.phonesms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;

import moe.shizuku.phonesms.databinding.ItemCustomerBinding;
import moe.shizuku.phonesms.entity.CustomerEntity;

public class CustomerAdapter extends RecyclerAdapter<ItemCustomerBinding> {
    private final List<CustomerEntity> customerEntities;

    public CustomerAdapter(Context context, List<CustomerEntity> entities) {
        super(context);
        this.customerEntities = entities;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CustomerEntity entity = customerEntities.get(position);
        ItemCustomerBinding binding = ItemCustomerBinding.bind(holder.itemView);
        binding.name.setText(entity.name);
        Glide.with(getContext()).load(entity.image).into(binding.image);
    }

    @Override
    public int getItemCount() {
        return customerEntities.size();
    }
}
