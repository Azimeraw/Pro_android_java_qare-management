/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.view.View
 *  android.webkit.WebSettings
 *  android.webkit.WebView
 *  android.webkit.WebViewClient
 *  androidx.appcompat.app.AppCompatActivity
 *  java.lang.String
 */
package com.example.prote;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class Profile
extends AppCompatActivity {
    WebView webView;

    protected void onCreate(Bundle bundle) {
        WebView webView;
        super.onCreate(bundle);
        this.setContentView(2131427358);
        this.webView = webView = (WebView)this.findViewById(2131230813);
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient());
        this.webView.loadUrl("file:///android_asset/profil.html");
    }
}

