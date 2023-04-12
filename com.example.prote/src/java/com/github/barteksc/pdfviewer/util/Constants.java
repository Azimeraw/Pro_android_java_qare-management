/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer.util;

public class Constants {
    public static boolean DEBUG_MODE = false;
    public static float PART_SIZE;
    public static int PRELOAD_COUNT;
    public static float THUMBNAIL_RATIO;

    static {
        THUMBNAIL_RATIO = 0.3f;
        PART_SIZE = 256.0f;
        PRELOAD_COUNT = 1;
    }

    public static class Cache {
        public static int CACHE_SIZE = 120;
        public static int THUMBNAILS_CACHE_SIZE = 6;
    }

    public static class Pinch {
        public static float MAXIMUM_ZOOM = 10.0f;
        public static float MINIMUM_ZOOM = 1.0f;
    }

}

