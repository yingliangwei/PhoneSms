package moe.shizuku.phonesms.adapter;

import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.databinding.ItemBuyVipBinding;
import moe.shizuku.phonesms.entity.BuyVipEntity;

public class BuyVipAdapter extends RecyclerAdapter<ItemBuyVipBinding> {
    private final List<BuyVipEntity> buyVipEntity;

    public BuyVipAdapter(Context context, List<BuyVipEntity> buyVipEntity) {
        super(context);
        this.buyVipEntity = buyVipEntity;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemBuyVipBinding binding = ItemBuyVipBinding.bind(holder.itemView);
        BuyVipEntity entity = buyVipEntity.get(position);
        binding.name.setText(entity.name);
        binding.price.setText(String.format("%s %s", getContext().getString(R.string.Original_price), entity.price));
        binding.price.setPaintFlags(binding.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.money.setText(String.format("%s %s", getContext().getString(R.string.Current_price), entity.money));
    }

    @Override
    public int getItemCount() {
        return buyVipEntity.size();
    }
}
