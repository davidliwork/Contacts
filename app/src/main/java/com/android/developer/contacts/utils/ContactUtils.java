package com.android.developer.contacts.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.developer.contacts.PinyinComparator;
import com.android.developer.contacts.SortModel;
import com.android.developer.contacts.SortToken;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by DavidLi on 2017-11-17.
 */

public class ContactUtils {

    /**
     * 中文字符串匹配
     */
    public static final String chReg = "[\\u4E00-\\u9FA5]+";

    public static List<SortModel> loadContacts(@NonNull Context context) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key"}, null, null, "sort_key COLLATE LOCALIZED ASC");
            if (phoneCursor == null || phoneCursor.getCount() == 0) {
                return null;
            }
            int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int SORT_KEY_INDEX = phoneCursor.getColumnIndex("sort_key");
            if (phoneCursor.getCount() > 0) {
                ArrayList<SortModel> allContactsList = new ArrayList<>();
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    String sortKey = phoneCursor.getString(SORT_KEY_INDEX);
                    //System.out.println(sortKey);
                    SortModel sortModel = new SortModel(contactName, phoneNumber, sortKey);
                    //优先使用系统sortkey取,取不到再使用工具取
                    String sortLetters = getSortLetterBySortKey(sortKey);
                    if (sortLetters == null || "#".equals(sortLetters)) {
                        sortLetters = getSortLetter(contactName);
                    }
                    sortModel.sortLetters = sortLetters;

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        sortModel.sortToken = parseSortKey(sortKey);
                    else
                        sortModel.sortToken = parseSortKeyLollipop(sortKey);

                    allContactsList.add(sortModel);
                }
                phoneCursor.close();
                Collections.sort(allContactsList, new PinyinComparator());
                return allContactsList;
            }
        } catch (Exception e) {
            Log.e("xbc", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 取sort_key的首字母
     *
     * @param sortKey
     * @return
     */
    public static String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }
        String letter = "#";
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 5.0以上需要判断汉字
            if (sortString.matches("^[\u4E00-\u9FFF]+$"))// 正则表达式，判断是否为汉字
                letter = getSortLetter(sortString.toUpperCase(Locale.CHINESE));
        }
        return letter;
    }

    /**
     * 名字转拼音,取首字母
     *
     * @param name
     * @return
     */
    public static String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        //汉字转换成拼音

        String pinyin = Pinyin.toPinyin(name, "");
        ;
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 解析sort_key,封装简拼,全拼
     *
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            //其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell.append(enStrs[i].charAt(0));
                    token.wholeSpell.append(enStrs[i]);
                }
            }
        }
        return token;
    }

    /**
     * 解析sort_key,封装简拼,全拼。
     * Android 5.0 以上使用
     *
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKeyLollipop(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            boolean isChinese = sortKey.matches(chReg);
            // 分割条件：中文不分割，英文以大写和空格分割
            String regularExpression = isChinese ? "" : "(?=[A-Z])|\\s";

            String[] enStrs = sortKey.split(regularExpression);

            for (int i = 0, length = enStrs.length; i < length; i++)
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell.append(getSortLetter(String.valueOf(enStrs[i].charAt(0))));
                    token.wholeSpell.append(Pinyin.toPinyin(enStrs[i], ""));
                }
        }
        return token;
    }

    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    public static List<SortModel> search(List<SortModel> allContactsList, String str) {
        List<SortModel> filterList = new ArrayList<>();// 过滤后的list
        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (SortModel contact : allContactsList) {
                if (contact.number != null && contact.name != null) {
                    if (contact.simpleNumber.contains(simpleStr) || contact.name.contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else if (allContactsList != null) {
            for (SortModel contact : allContactsList) {
                if (contact.number != null && contact.name != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    boolean isNameContains = contact.name.toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isSortKeyContains = contact.sortKey.toLowerCase(Locale.CHINESE).replace(" ", "")
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isSimpleSpellContains = contact.sortToken.simpleSpell.toString().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isWholeSpellContains = contact.sortToken.wholeSpell.toString().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    if (isNameContains || isSortKeyContains || isSimpleSpellContains || isWholeSpellContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

}
