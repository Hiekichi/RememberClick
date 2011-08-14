package com.hiekichi.sample.marubatsu;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class MaruBatsuActivity extends Activity {

	MBView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        view = new MBView(this);
        setContentView(view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}