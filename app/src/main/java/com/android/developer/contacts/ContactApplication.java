package com.android.developer.contacts;

import android.app.Application;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;

/**
 * Created by DavidLi on 2017-11-18.
 */

public class ContactApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)));
    }
}
