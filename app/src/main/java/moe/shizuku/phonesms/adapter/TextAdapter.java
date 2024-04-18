package moe.shizuku.phonesms.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import moe.shizuku.phonesms.databinding.ItemSmsTextBinding;
import moe.shizuku.phonesms.entity.SmsEntity;
import moe.shizuku.phonesms.util.TimeUtils;


public class TextAdapter extends RecyclerAdapter<ItemSmsTextBinding> {
    private final List<SmsEntity> texts;

    public TextAdapter(Context context, List<SmsEntity> strings) {
        super(context);
        this.texts = strings;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemSmsTextBinding binding = ItemSmsTextBinding.bind(viewHolder.itemView);
        SmsEntity smsEntity = texts.get(i);
        if (smsEntity.isSend == 1) {
            binding.time.setText(TimeUtils.getTimeFromTimestamp(getContext(), smsEntity.time));
            binding.text.setText(smsEntity.message);
        } else {
            binding.root.setVisibility(View.GONE);
            binding.root1.setVisibility(View.VISIBLE);
            binding.text2.setText(smsEntity.message);
            binding.time1.setText(TimeUtils.getTimeFromTimestamp(getContext(), smsEntity.time));
        }
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }
}
