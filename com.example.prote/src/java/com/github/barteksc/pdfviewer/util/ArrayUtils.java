/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 */
package com.github.barteksc.pdfviewer.util;

import java.util.ArrayList;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static String arrayToString(int[] arrn) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < arrn.length; ++i) {
            stringBuilder.append(arrn[i]);
            if (i == -1 + arrn.length) continue;
            stringBuilder.append(",");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static int[] calculateIndexesInDuplicateArray(int[] arrn) {
        int[] arrn2 = new int[arrn.length];
        if (arrn.length == 0) {
            return arrn2;
        }
        int n = 0;
        arrn2[0] = 0;
        for (int i = 1; i < arrn.length; ++i) {
            if (arrn[i] != arrn[i - 1]) {
                // empty if block
            }
            arrn2[i] = ++n;
        }
        return arrn2;
    }

    public static int[] deleteDuplicatedPages(int[] arrn) {
        ArrayList arrayList = new ArrayList();
        int n = -1;
        int n2 = arrn.length;
        for (int i = 0; i < n2; ++i) {
            Integer n3 = arrn[i];
            if (n != n3) {
                arrayList.add((Object)n3);
            }
            n = n3;
        }
        int[] arrn2 = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); ++i) {
            arrn2[i] = (Integer)arrayList.get(i);
        }
        return arrn2;
    }
}

