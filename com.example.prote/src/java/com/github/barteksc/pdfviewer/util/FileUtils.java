/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.AssetManager
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 */
package com.github.barteksc.pdfviewer.util;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private FileUtils() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void copy(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            int n;
            fileOutputStream = new FileOutputStream(file);
            byte[] arrby = new byte[1024];
            while ((n = inputStream.read(arrby)) != -1) {
                fileOutputStream.write(arrby, 0, n);
            }
            if (inputStream == null) return;
        }
        catch (Throwable throwable) {
            if (inputStream == null) throw throwable;
            try {
                inputStream.close();
                throw throwable;
            }
            finally {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
        }
        try {
            inputStream.close();
            return;
        }
        finally {
            fileOutputStream.close();
        }
    }

    public static File fileFromAsset(Context context, String string2) throws IOException {
        File file = context.getCacheDir();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        stringBuilder.append("-pdfview.pdf");
        File file2 = new File(file, stringBuilder.toString());
        if (string2.contains((CharSequence)"/")) {
            file2.getParentFile().mkdirs();
        }
        FileUtils.copy(context.getAssets().open(string2), file2);
        return file2;
    }
}

