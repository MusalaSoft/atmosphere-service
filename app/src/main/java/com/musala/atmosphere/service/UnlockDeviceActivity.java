package com.musala.atmosphere.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * 
 * @author denis.bialev
 * 
 */
public class UnlockDeviceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_device);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // When brought to front unlocks the device if it is locked.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
