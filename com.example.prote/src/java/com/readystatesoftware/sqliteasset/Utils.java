/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Scanner
 *  java.util.zip.ZipEntry
 *  java.util.zip.ZipInputStream
 */
package com.readystatesoftware.sqliteasset;

import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Utils {
    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();

    Utils() {
    }

    public static String convertStreamToString(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }

    public static ZipInputStream getFileFromZip(InputStream inputStream) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        if (zipEntry != null) {
            String string2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("extracting file: '");
            stringBuilder.append(zipEntry.getName());
            stringBuilder.append("'...");
            Log.w((String)string2, (String)stringBuilder.toString());
            return zipInputStream;
        }
        return null;
    }

    public static List<String> splitSqlScript(String string2, char c) {
        ArrayList arrayList = new ArrayList();
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = false;
        char[] arrc = string2.toCharArray();
        for (int i = 0; i < string2.length(); ++i) {
            if (arrc[i] == '\"') {
                boolean bl2 = !bl;
                bl = bl2;
            }
            if (arrc[i] == c && !bl) {
                if (stringBuilder.length() <= 0) continue;
                arrayList.add((Object)stringBuilder.toString().trim());
                stringBuilder = new StringBuilder();
                continue;
            }
            stringBuilder.append(arrc[i]);
        }
        if (stringBuilder.length() > 0) {
            arrayList.add((Object)stringBuilder.toString().trim());
        }
        return arrayList;
    }

    public static void writeExtractedFileToDisk(InputStream inputStream, OutputStream outputStream) throws IOException {
        int n;
        byte[] arrby = new byte[1024];
        while ((n = inputStream.read(arrby)) > 0) {
            outputStream.write(arrby, 0, n);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
}

