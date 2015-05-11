package com.howtosayit.howtosayit2;


import android.test.ActivityInstrumentationTestCase2;

import com.howtosayit.howtosayit2.view.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }
}


