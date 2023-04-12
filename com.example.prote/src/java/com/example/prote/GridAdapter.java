/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.widget.BaseAdapter
 *  android.widget.ImageView
 *  android.widget.TextView
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 */
package com.example.prote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter
extends BaseAdapter {
    Context context;
    int[] image;
    LayoutInflater inflater;
    String[] name;

    public GridAdapter(Context context, String[] arrstring, int[] arrn) {
        this.context = context;
        this.name = arrstring;
        this.image = arrn;
    }

    public int getCount() {
        return this.name.length;
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0L;
    }

    public View getView(int n, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater;
        this.inflater = layoutInflater = (LayoutInflater)this.context.getSystemService("layout_inflater");
        View view2 = layoutInflater.inflate(2131427381, viewGroup, false);
        TextView textView = (TextView)view2.findViewById(2131230882);
        ImageView imageView = (ImageView)view2.findViewById(2131230881);
        textView.setText((CharSequence)this.name[n]);
        imageView.setImageResource(this.image[n]);
        return view2;
    }
}

