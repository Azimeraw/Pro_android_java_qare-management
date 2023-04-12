/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.webkit.WebView
 *  android.widget.GridView
 *  androidx.fragment.app.Fragment
 *  java.lang.String
 */
package com.example.prote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.GridView;
import androidx.fragment.app.Fragment;
import com.example.prote.GridAdapter;

public class Frag4
extends Fragment {
    GridAdapter gridAdapter;
    GridView gridView;
    int[] image;
    String[] name;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(2131427380, viewGroup, false);
        ((WebView)view.findViewById(2131231054)).loadUrl("file:///android_asset/samycontactlist.html");
        return view;
    }
}

