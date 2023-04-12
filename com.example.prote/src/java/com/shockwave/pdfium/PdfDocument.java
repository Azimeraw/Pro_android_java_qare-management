/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.ParcelFileDescriptor
 *  androidx.collection.ArrayMap
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 */
package com.shockwave.pdfium;

import android.os.ParcelFileDescriptor;
import androidx.collection.ArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfDocument {
    long mNativeDocPtr;
    final Map<Integer, Long> mNativePagesPtr = new ArrayMap();
    ParcelFileDescriptor parcelFileDescriptor;

    PdfDocument() {
    }

    public boolean hasPage(int n) {
        return this.mNativePagesPtr.containsKey((Object)n);
    }

    public static class Bookmark {
        private List<Bookmark> children = new ArrayList();
        long mNativePtr;
        long pageIdx;
        String title;

        public List<Bookmark> getChildren() {
            return this.children;
        }

        public long getPageIdx() {
            return this.pageIdx;
        }

        public String getTitle() {
            return this.title;
        }

        public boolean hasChildren() {
            return true ^ this.children.isEmpty();
        }
    }

    public static class Meta {
        String author;
        String creationDate;
        String creator;
        String keywords;
        String modDate;
        String producer;
        String subject;
        String title;

        public String getAuthor() {
            return this.author;
        }

        public String getCreationDate() {
            return this.creationDate;
        }

        public String getCreator() {
            return this.creator;
        }

        public String getKeywords() {
            return this.keywords;
        }

        public String getModDate() {
            return this.modDate;
        }

        public String getProducer() {
            return this.producer;
        }

        public String getSubject() {
            return this.subject;
        }

        public String getTitle() {
            return this.title;
        }
    }

}

