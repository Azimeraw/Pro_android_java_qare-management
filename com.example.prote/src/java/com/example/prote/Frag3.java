/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  androidx.fragment.app.Fragment
 *  java.lang.String
 */
package com.example.prote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;

public class Frag3
extends Fragment {
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(2131427378, viewGroup, false);
        ((PDFView)view.findViewById(2131230811)).fromAsset("aser.pdf").load();
        return view;
    }
}

