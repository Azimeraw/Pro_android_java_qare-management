/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.graphics.Bitmap
 *  android.os.ParcelFileDescriptor
 *  android.util.DisplayMetrics
 *  android.util.Log
 *  android.view.Surface
 *  java.io.FileDescriptor
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.IllegalAccessException
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.NoSuchFieldException
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.lang.UnsatisfiedLinkError
 *  java.lang.reflect.Field
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 */
package com.shockwave.pdfium;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import com.shockwave.pdfium.PdfDocument;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfiumCore {
    private static final Class FD_CLASS;
    private static final String FD_FIELD_NAME = "descriptor";
    private static final String TAG;
    private static final Object lock;
    private static Field mFdField;
    private int mCurrentDpi;

    static {
        TAG = PdfiumCore.class.getName();
        try {
            System.loadLibrary((String)"modpng");
            System.loadLibrary((String)"modft2");
            System.loadLibrary((String)"modpdfium");
            System.loadLibrary((String)"jniPdfium");
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            String string2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Native libraries failed to load - ");
            stringBuilder.append((Object)unsatisfiedLinkError);
            Log.e((String)string2, (String)stringBuilder.toString());
        }
        FD_CLASS = FileDescriptor.class;
        mFdField = null;
        lock = new Object();
    }

    public PdfiumCore(Context context) {
        this.mCurrentDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getNumFd(ParcelFileDescriptor parcelFileDescriptor) {
        try {
            if (mFdField == null) {
                Field field;
                mFdField = field = FD_CLASS.getDeclaredField(FD_FIELD_NAME);
                field.setAccessible(true);
            }
            int n = mFdField.getInt((Object)parcelFileDescriptor.getFileDescriptor());
            return n;
        }
        catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
            return -1;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
            return -1;
        }
    }

    private native void nativeCloseDocument(long var1);

    private native void nativeClosePage(long var1);

    private native void nativeClosePages(long[] var1);

    private native long nativeGetBookmarkDestIndex(long var1, long var3);

    private native String nativeGetBookmarkTitle(long var1);

    private native String nativeGetDocumentMetaText(long var1, String var3);

    private native Long nativeGetFirstChildBookmark(long var1, Long var3);

    private native int nativeGetPageCount(long var1);

    private native int nativeGetPageHeightPixel(long var1, int var3);

    private native int nativeGetPageHeightPoint(long var1);

    private native int nativeGetPageWidthPixel(long var1, int var3);

    private native int nativeGetPageWidthPoint(long var1);

    private native Long nativeGetSiblingBookmark(long var1, long var3);

    private native long nativeLoadPage(long var1, int var3);

    private native long[] nativeLoadPages(long var1, int var3, int var4);

    private native long nativeOpenDocument(int var1, String var2);

    private native long nativeOpenMemDocument(byte[] var1, String var2);

    private native void nativeRenderPage(long var1, Surface var3, int var4, int var5, int var6, int var7, int var8, boolean var9);

    private native void nativeRenderPageBitmap(long var1, Bitmap var3, int var4, int var5, int var6, int var7, int var8, boolean var9);

    private void recursiveGetBookmark(List<PdfDocument.Bookmark> list, PdfDocument pdfDocument, long l) {
        Long l2;
        PdfDocument.Bookmark bookmark = new PdfDocument.Bookmark();
        bookmark.mNativePtr = l;
        bookmark.title = this.nativeGetBookmarkTitle(l);
        bookmark.pageIdx = this.nativeGetBookmarkDestIndex(pdfDocument.mNativeDocPtr, l);
        list.add((Object)bookmark);
        Long l3 = this.nativeGetFirstChildBookmark(pdfDocument.mNativeDocPtr, l);
        if (l3 != null) {
            this.recursiveGetBookmark(bookmark.getChildren(), pdfDocument, l3);
        }
        if ((l2 = this.nativeGetSiblingBookmark(pdfDocument.mNativeDocPtr, l)) != null) {
            this.recursiveGetBookmark(list, pdfDocument, l2);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void closeDocument(PdfDocument pdfDocument) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            for (Integer n : pdfDocument.mNativePagesPtr.keySet()) {
                this.nativeClosePage((Long)pdfDocument.mNativePagesPtr.get((Object)n));
            }
            pdfDocument.mNativePagesPtr.clear();
            this.nativeCloseDocument(pdfDocument.mNativeDocPtr);
            ParcelFileDescriptor parcelFileDescriptor = pdfDocument.parcelFileDescriptor;
            if (parcelFileDescriptor == null) return;
            try {
                pdfDocument.parcelFileDescriptor.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            pdfDocument.parcelFileDescriptor = null;
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public PdfDocument.Meta getDocumentMeta(PdfDocument pdfDocument) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            PdfDocument.Meta meta = new PdfDocument.Meta();
            meta.title = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Title");
            meta.author = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Author");
            meta.subject = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Subject");
            meta.keywords = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Keywords");
            meta.creator = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Creator");
            meta.producer = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "Producer");
            meta.creationDate = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "CreationDate");
            meta.modDate = this.nativeGetDocumentMetaText(pdfDocument.mNativeDocPtr, "ModDate");
            return meta;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getPageCount(PdfDocument pdfDocument) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            return this.nativeGetPageCount(pdfDocument.mNativeDocPtr);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getPageHeight(PdfDocument pdfDocument, int n) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            Long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
            if (l == null) return 0;
            return this.nativeGetPageHeightPixel(l, this.mCurrentDpi);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getPageHeightPoint(PdfDocument pdfDocument, int n) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            Long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
            if (l == null) return 0;
            return this.nativeGetPageHeightPoint(l);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getPageWidth(PdfDocument pdfDocument, int n) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            Long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
            if (l == null) return 0;
            return this.nativeGetPageWidthPixel(l, this.mCurrentDpi);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getPageWidthPoint(PdfDocument pdfDocument, int n) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            Long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
            if (l == null) return 0;
            return this.nativeGetPageWidthPoint(l);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<PdfDocument.Bookmark> getTableOfContents(PdfDocument pdfDocument) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            ArrayList arrayList = new ArrayList();
            Long l = this.nativeGetFirstChildBookmark(pdfDocument.mNativeDocPtr, null);
            if (l != null) {
                this.recursiveGetBookmark((List<PdfDocument.Bookmark>)arrayList, pdfDocument, l);
            }
            return arrayList;
        }
    }

    public PdfDocument newDocument(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        return this.newDocument(parcelFileDescriptor, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public PdfDocument newDocument(ParcelFileDescriptor parcelFileDescriptor, String string2) throws IOException {
        Object object;
        PdfDocument pdfDocument = new PdfDocument();
        pdfDocument.parcelFileDescriptor = parcelFileDescriptor;
        Object object2 = object = lock;
        synchronized (object2) {
            pdfDocument.mNativeDocPtr = this.nativeOpenDocument(PdfiumCore.getNumFd(parcelFileDescriptor), string2);
            return pdfDocument;
        }
    }

    public PdfDocument newDocument(byte[] arrby) throws IOException {
        return this.newDocument(arrby, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public PdfDocument newDocument(byte[] arrby, String string2) throws IOException {
        Object object;
        PdfDocument pdfDocument = new PdfDocument();
        Object object2 = object = lock;
        synchronized (object2) {
            pdfDocument.mNativeDocPtr = this.nativeOpenMemDocument(arrby, string2);
            return pdfDocument;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public long openPage(PdfDocument pdfDocument, int n) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            long l = this.nativeLoadPage(pdfDocument.mNativeDocPtr, n);
            pdfDocument.mNativePagesPtr.put((Object)n, (Object)l);
            return l;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public long[] openPage(PdfDocument pdfDocument, int n, int n2) {
        Object object;
        Object object2 = object = lock;
        synchronized (object2) {
            long[] arrl = this.nativeLoadPages(pdfDocument.mNativeDocPtr, n, n2);
            int n3 = n;
            int n4 = arrl.length;
            for (int i = 0; i < n4; ++n3, ++i) {
                long l = arrl[i];
                if (n3 > n2) break;
                pdfDocument.mNativePagesPtr.put((Object)n3, (Object)l);
            }
            return arrl;
        }
    }

    public void renderPage(PdfDocument pdfDocument, Surface surface, int n, int n2, int n3, int n4, int n5) {
        this.renderPage(pdfDocument, surface, n, n2, n3, n4, n5, false);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void renderPage(PdfDocument pdfDocument, Surface surface, int n, int n2, int n3, int n4, int n5, boolean bl) {
        Object object;
        void var11_19;
        Object object2 = object = lock;
        // MONITORENTER : object2
        long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
        try {
            void var10_16;
            block11 : {
                void var13_14;
                block10 : {
                    try {
                        this.nativeRenderPage(l, surface, this.mCurrentDpi, n2, n3, n4, n5, bl);
                        return;
                    }
                    catch (Exception exception) {
                        break block10;
                    }
                    catch (NullPointerException nullPointerException) {
                        break block11;
                    }
                    catch (Throwable throwable) {
                        throw var11_19;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                Log.e((String)TAG, (String)"Exception throw from native");
                var13_14.printStackTrace();
                return;
                catch (NullPointerException nullPointerException) {}
            }
            Log.e((String)TAG, (String)"mContext may be null");
            var10_16.printStackTrace();
            return;
        }
        catch (Throwable throwable) {
            throw var11_19;
        }
    }

    public void renderPageBitmap(PdfDocument pdfDocument, Bitmap bitmap, int n, int n2, int n3, int n4, int n5) {
        this.renderPageBitmap(pdfDocument, bitmap, n, n2, n3, n4, n5, false);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void renderPageBitmap(PdfDocument pdfDocument, Bitmap bitmap, int n, int n2, int n3, int n4, int n5, boolean bl) {
        Object object;
        void var11_19;
        Object object2 = object = lock;
        // MONITORENTER : object2
        long l = (Long)pdfDocument.mNativePagesPtr.get((Object)n);
        try {
            void var10_16;
            block11 : {
                void var13_14;
                block10 : {
                    try {
                        this.nativeRenderPageBitmap(l, bitmap, this.mCurrentDpi, n2, n3, n4, n5, bl);
                        return;
                    }
                    catch (Exception exception) {
                        break block10;
                    }
                    catch (NullPointerException nullPointerException) {
                        break block11;
                    }
                    catch (Throwable throwable) {
                        throw var11_19;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                Log.e((String)TAG, (String)"Exception throw from native");
                var13_14.printStackTrace();
                return;
                catch (NullPointerException nullPointerException) {}
            }
            Log.e((String)TAG, (String)"mContext may be null");
            var10_16.printStackTrace();
            return;
        }
        catch (Throwable throwable) {
            throw var11_19;
        }
    }
}

