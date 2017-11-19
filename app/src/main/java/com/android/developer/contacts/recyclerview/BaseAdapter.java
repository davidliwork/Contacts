package com.android.developer.contacts.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by DavidLi on 2017-11-17.
 */

public abstract class BaseAdapter<VH extends BaseHolder, D> extends RecyclerView.Adapter<VH>
        implements View.OnClickListener, View.OnLongClickListener {
    protected List<D> mData;

    public void loadData(List<D> data) {
        this.mData = data;
    }

    private ItemClickListener listener;
    private ItemLongClickListener longClicklistener;
    private ItemChildClickListener itemChildClickListener;

    @Override
    public boolean onLongClick(View view) {
        if (longClicklistener != null) {
            int position = (int) view.getTag();
            longClicklistener.onItemLongClick(view, position);
        }
        return longClicklistener == null;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface ItemChildClickListener {
        void onItemChildClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            listener.onItemClick(v, holder.getLayoutPosition());
        }
    }

    public List<D> getData() {
        return mData;
    }

    public D getItem(int position) {
        return mData.get(position);
    }

    /**
     * 条目监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 条目监听
     *
     * @param listener 监听器
     */
    public void setOnItemLongClickListener(ItemLongClickListener listener) {
        this.longClicklistener = listener;
    }

    public void setOnItemChildClickListener(ItemChildClickListener listener) {
        this.itemChildClickListener = listener;
    }

    public ItemChildClickListener getItemChildClickListener() {
        return itemChildClickListener;
    }
}
