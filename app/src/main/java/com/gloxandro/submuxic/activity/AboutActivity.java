package com.gloxandro.submuxic.activity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.colors.Constants;

public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constants.LOG_V)
            Log.v(TAG, "onCreate(" + savedInstanceState + ")");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        WebView aboutWebView = (WebView) findViewById(R.id.aboutWebView);
        aboutWebView.setBackgroundColor(0);
        aboutWebView.loadUrl("file:///android_asset/about_light.html");
    }
}