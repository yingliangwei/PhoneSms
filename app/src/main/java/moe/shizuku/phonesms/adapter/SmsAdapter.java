package moe.shizuku.phonesms.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import moe.shizuku.phonesms.databinding.ItemSmsBinding;
import moe.shizuku.phonesms.entity.SmsEntity;
import moe.shizuku.phonesms.sms.activity.SmsDetailedActivity;
import moe.shizuku.phonesms.util.TimeUtils;

public class SmsAdapter extends RecyclerAdapter<ItemSmsBinding> {
    private final List<SmsEntity> smsEntities;

    public SmsAdapter(Context context, List<SmsEntity> smsEntities) {
        super(context);
        this.smsEntities = smsEntities;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemSmsBinding binding = ItemSmsBinding.bind(viewHolder.itemView);
        SmsEntity smsEntity = smsEntities.get(i);
        binding.title.setText(smsEntity.title);
        binding.message.setText(smsEntity.message);
        binding.time.setText(TimeUtils.getTimeFromTimestamp(getContext(), smsEntity.time));
    }

    @Override
    public int getItemCount() {
        return smsEntities.size();
    }
}
