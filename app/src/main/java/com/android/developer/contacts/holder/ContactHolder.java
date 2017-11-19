package com.android.developer.contacts.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.developer.contacts.ContactsSortAdapter;
import com.android.developer.contacts.R;
import com.android.developer.contacts.SortModel;
import com.android.developer.contacts.recyclerview.BaseHolder;

import butterknife.BindView;

/**
 * Created by DavidLi on 2017-11-18.
 */

public class ContactHolder extends BaseHolder<SortModel> {

    @BindView(R.id.catalog)
    public TextView tvLetter;
    @BindView(R.id.title)
    public TextView tvTitle;
    @BindView(R.id.number)
    public TextView tvNumber;
    @BindView(R.id.cbChecked)
    public CheckBox cbChecked;

    public ContactHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(SortModel data) {
        super.setData(data);
        ContactsSortAdapter adapter = (ContactsSortAdapter) mAdapter;
        //根据position获取分类的首字母的Char ascii值
        int section = adapter.getSectionForPosition(getLayoutPosition());

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (getLayoutPosition() == adapter.getPositionForSection(section)) {
            tvLetter.setVisibility(View.VISIBLE);
            tvLetter.setText(data.sortLetters);
        } else {
            tvLetter.setVisibility(View.GONE);
        }

        tvTitle.setText(data.name);
        tvNumber.setText(data.number);
        cbChecked.setChecked(adapter.isSelected(data));
    }
}
