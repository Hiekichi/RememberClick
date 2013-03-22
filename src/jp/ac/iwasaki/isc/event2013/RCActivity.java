package jp.ac.iwasaki.isc.event2013;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class RCActivity extends Activity {

	RCView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        view = new RCView(this);
        setContentView(view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}