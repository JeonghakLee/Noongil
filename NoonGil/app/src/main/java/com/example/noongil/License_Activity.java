package com.example.noongil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class License_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_);

        WebView webView = (WebView)findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/license.html");
    }
}