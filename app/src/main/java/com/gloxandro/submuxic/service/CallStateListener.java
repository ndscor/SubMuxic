package com.gloxandro.submuxic.service;

import android.os.Build;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.S)
public class CallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
    private String TAG = "MyCallStateListener";
    @Override
    public void onCallStateChanged(int state) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: {
                Log.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&& state is CALL_STATE_IDLE");
                break;
            }
            case TelephonyManager.CALL_STATE_OFFHOOK: {
                Log.v(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&& state is CALL_STATE_OFFHOOK");
                break;
            }
            default: {
                Log.v(TAG, "state is " + state);
            }
        }
    }
}
