/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.util.DisplayMetrics
 *  android.util.TypedValue
 *  java.io.ByteArrayOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static int getDP(Context context, int n) {
        return (int)TypedValue.applyDimension((int)1, (float)n, (DisplayMetrics)context.getResources().getDisplayMetrics());
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        int n;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrby = new byte[4096];
        while (-1 != (n = inputStream.read(arrby))) {
            byteArrayOutputStream.write(arrby, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }
}

