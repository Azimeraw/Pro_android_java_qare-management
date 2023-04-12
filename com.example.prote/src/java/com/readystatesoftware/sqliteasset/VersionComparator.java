/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Comparator
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 */
package com.readystatesoftware.sqliteasset;

import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VersionComparator
implements Comparator<String> {
    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
    private Pattern pattern = Pattern.compile((String)".*_upgrade_([0-9]+)-([0-9]+).*");

    VersionComparator() {
    }

    public int compare(String string2, String string3) {
        Matcher matcher = this.pattern.matcher((CharSequence)string2);
        Matcher matcher2 = this.pattern.matcher((CharSequence)string3);
        if (matcher.matches()) {
            if (matcher2.matches()) {
                int n = 1;
                int n2 = Integer.valueOf((String)matcher.group(n));
                int n3 = Integer.valueOf((String)matcher2.group(n));
                int n4 = Integer.valueOf((String)matcher.group(2));
                int n5 = Integer.valueOf((String)matcher2.group(2));
                if (n2 == n3) {
                    if (n4 == n5) {
                        return 0;
                    }
                    if (n4 < n5) {
                        n = -1;
                    }
                    return n;
                }
                if (n2 < n3) {
                    n = -1;
                }
                return n;
            }
            String string4 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("could not parse upgrade script file: ");
            stringBuilder.append(string3);
            Log.w((String)string4, (String)stringBuilder.toString());
            throw new SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file");
        }
        String string5 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("could not parse upgrade script file: ");
        stringBuilder.append(string2);
        Log.w((String)string5, (String)stringBuilder.toString());
        throw new SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file");
    }
}

