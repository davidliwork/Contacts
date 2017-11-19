package com.android.developer.contacts.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.developer.contacts.ContactsSortAdapter;
import com.android.developer.contacts.R;
import com.android.developer.contacts.SortModel;
import com.android.developer.contacts.base.BaseActivity;
import com.android.developer.contacts.base.BaseObserver;
import com.android.developer.contacts.recyclerview.BaseAdapter;
import com.android.developer.contacts.utils.ContactUtils;
import com.android.developer.contacts.widget.SideBar;

import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactsActivity extends BaseActivity implements BaseAdapter.ItemClickListener, EasyPermissions.PermissionCallbacks, SideBar.OnTouchingLetterChangedListener, TextWatcher {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.ivClearText)
    ImageView ivClearText;
    @BindView(R.id.sidebar)
    SideBar sideBar;
    @BindView(R.id.dialog)
    TextView dialog;

    private ContactsSortAdapter mAdapter;
    private List<SortModel> mAllContactsList;
    private LinearLayoutManager mLayoutManager;
    private static final int READ_CONTACTS_PERM = 100;
    private Subscription mSearchSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_contacts_layout;
    }

    private void init() {
        initView();
        initListener();
        loadContacts();
    }

    private void initListener() {

        /**清除输入字符**/
        ivClearText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(this);

        //设置右侧[A-Z]快速导航栏触摸监听
        sideBar.setOnTouchingLetterChangedListener(this);
    }

    private void initView() {
        sideBar.setTextView(dialog);
        /** 给ListView设置adapter **/
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new ContactsSortAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @AfterPermissionGranted(READ_CONTACTS_PERM)
    private void loadContacts() {
        if (hasContactPermission()) {
            Observable.just(ContactUtils.loadContacts(getApplicationContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<List<SortModel>>() {
                        @Override
                        public void onNext(List<SortModel> sortModels) {
                            super.onNext(sortModels);
                            if (sortModels != null && sortModels.size() > 0) {
                                mAllContactsList = sortModels;
                                mAdapter.loadData(sortModels);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.contact_is_empty, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            Toast.makeText(getApplicationContext(), R.string.contact_load_error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.contact_permission),
                    READ_CONTACTS_PERM,
                    Manifest.permission.READ_CONTACTS);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mAdapter.toggleChecked(position);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private boolean hasContactPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(final Editable s) {
        String content = etSearch.getText().toString();
        if ("".equals(content)) {
            ivClearText.setVisibility(View.INVISIBLE);
        } else {
            ivClearText.setVisibility(View.VISIBLE);
        }
        if (content.length() > 0) {
            if (mSearchSubscribe != null) {
                mSearchSubscribe.unsubscribe();
            }
            mSearchSubscribe = Observable.just(ContactUtils.search(mAllContactsList, content))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new BaseObserver<List<SortModel>>() {
                        @Override
                        public void onNext(List<SortModel> sortModels) {
                            super.onNext(sortModels);
                            mAdapter.loadData(sortModels);
                            mAdapter.notifyDataSetChanged();
                            mLayoutManager.scrollToPositionWithOffset(0, 0);
                        }
                    });
        } else {
            mAdapter.loadData(mAllContactsList);
            mAdapter.notifyDataSetChanged();
            mLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

}