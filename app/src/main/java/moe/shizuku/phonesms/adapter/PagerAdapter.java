package moe.shizuku.phonesms.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<RecyclerAdapter<?>> recyclerAdapters;

    public PagerAdapter(List<RecyclerAdapter<?>> recyclerAdapters) {
        this.recyclerAdapters = recyclerAdapters;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return recyclerAdapters.get(i).onCreateViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        recyclerAdapters.get(i).onBindViewHolder(viewHolder, i);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        recyclerAdapters.get(holder.getAdapterPosition()).onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        recyclerAdapters.get(holder.getAdapterPosition()).onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return recyclerAdapters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ViewBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
