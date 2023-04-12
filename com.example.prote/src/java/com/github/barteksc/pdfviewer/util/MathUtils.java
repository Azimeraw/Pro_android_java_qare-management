/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer.util;

public class MathUtils {
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_FLOOR = 16384.0;
    private static final int BIG_ENOUGH_INT = 16384;

    private MathUtils() {
    }

    public static int ceil(float f) {
        double d = f;
        Double.isNaN((double)d);
        return -16384 + (int)(d + 16384.999999999996);
    }

    public static int floor(float f) {
        double d = f;
        Double.isNaN((double)d);
        return -16384 + (int)(d + 16384.0);
    }

    public static float limit(float f, float f2, float f3) {
        if (f <= f2) {
            return f2;
        }
        if (f >= f3) {
            return f3;
        }
        return f;
    }

    public static int limit(int n, int n2, int n3) {
        if (n <= n2) {
            return n2;
        }
        if (n >= n3) {
            return n3;
        }
        return n;
    }

    public static float max(float f, float f2) {
        if (f > f2) {
            return f2;
        }
        return f;
    }

    public static int max(int n, int n2) {
        if (n > n2) {
            return n2;
        }
        return n;
    }

    public static float min(float f, float f2) {
        if (f < f2) {
            return f2;
        }
        return f;
    }

    public static int min(int n, int n2) {
        if (n < n2) {
            return n2;
        }
        return n;
    }
}

