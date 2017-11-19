package com.android.developer.contacts.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by DavidLi on 2017-11-17.
 */

public abstract class BaseHolder<D> extends RecyclerView.ViewHolder {
    protected D mData;
    protected Context mContext;
    protected BaseAdapter mAdapter;

    public BaseHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setData(D data) {
        mData = data;
    }

    public D getData() {
        return mData;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
    }
}
