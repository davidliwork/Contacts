package com.android.developer.contacts;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.android.developer.contacts.holder.ContactHolder;
import com.android.developer.contacts.recyclerview.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactsSortAdapter extends BaseAdapter<ContactHolder, SortModel> implements SectionIndexer {
    private List<SortModel> mSelectedList;

    public ContactsSortAdapter() {
        mSelectedList = new ArrayList<SortModel>();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContactHolder contactHolder = new ContactHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
        contactHolder.setAdapter(this);
        return contactHolder;
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 根据当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).sortLetters.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public boolean isSelected(SortModel model) {
        return mSelectedList.contains(model);
    }

    public void toggleChecked(int position) {
        if (isSelected(mData.get(position))) {
            removeSelected(position);
        } else {
            addSelected(position);
        }
    }

    private void addSelected(int position) {
        if (!mSelectedList.contains(mData.get(position))) {
            mSelectedList.add(mData.get(position));
        }
    }

    private void removeSelected(int position) {
        if (mSelectedList.contains(mData.get(position))) {
            mSelectedList.remove(mData.get(position));
        }
    }

    public List<SortModel> getSelectedList() {
        return mSelectedList;
    }
}