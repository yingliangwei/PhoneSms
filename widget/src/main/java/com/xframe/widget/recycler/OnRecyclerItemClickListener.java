package com.xframe.widget.recycler;

import com.xframe.widget.entity.RecyclerEntity;

public interface OnRecyclerItemClickListener {
    void onItemClick(RecyclerEntity entity, int position);

    default void onItemClick(RecyclerEntity entity, int position, boolean box) {

    }

}
