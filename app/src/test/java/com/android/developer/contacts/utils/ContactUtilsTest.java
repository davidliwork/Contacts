package com.android.developer.contacts.utils;

import org.junit.Test;

/**
 * Created by DavidLi on 2017-11-18.
 */
public class ContactUtilsTest {
    @Test
    public void getSortLetter() throws Exception {
        String name = "郭旻彤";
        String sortLetter = ContactUtils.getSortLetter(name);
        System.out.println("sortLetter:" + sortLetter);
    }

}