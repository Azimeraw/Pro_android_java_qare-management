/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.webkit.WebSettings
 *  android.webkit.WebView
 *  android.webkit.WebViewClient
 *  androidx.fragment.app.Fragment
 *  com.google.android.gms.ads.AdRequest
 *  com.google.android.gms.ads.AdRequest$Builder
 *  com.google.android.gms.ads.AdView
 *  java.lang.String
 */
package com.example.prote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Frag1
extends Fragment {
    private AdView nadView;
    WebView nbweb;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        WebView webView;
        View view = layoutInflater.inflate(2131427377, viewGroup, false);
        this.nadView = (AdView)view.findViewById(2131230784);
        AdRequest adRequest = new AdRequest.Builder().build();
        this.nadView.loadAd(adRequest);
        this.nbweb = webView = (WebView)view.findViewById(2131230812);
        webView.getSettings().setJavaScriptEnabled(true);
        this.nbweb.setWebViewClient(new WebViewClient());
        this.nbweb.loadUrl("file:///android_asset/t.html");
        return view;
    }
}

