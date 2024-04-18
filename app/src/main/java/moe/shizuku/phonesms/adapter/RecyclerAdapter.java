package moe.shizuku.phonesms.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import moe.shizuku.phonesms.util.ViewBindingUtil;

public class RecyclerAdapter<T extends ViewBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private T binding;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    public Context getContext() {
        return context;
    }

    public T getBinding() {
        return binding;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = ViewBindingUtil.inflate(getClass(), getLayoutInflater(), viewGroup, false);
        return new PagerAdapter.ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(receiver, filter);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
