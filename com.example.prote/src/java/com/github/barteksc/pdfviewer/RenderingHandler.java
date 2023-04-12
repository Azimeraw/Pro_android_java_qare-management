/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  android.graphics.Matrix
 *  android.graphics.Rect
 *  android.graphics.RectF
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.Message
 *  android.util.SparseBooleanArray
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 */
package com.github.barteksc.pdfviewer;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseBooleanArray;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.exception.PageRenderingException;
import com.github.barteksc.pdfviewer.model.PagePart;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

class RenderingHandler
extends Handler {
    static final int MSG_RENDER_TASK = 1;
    private static final String TAG = RenderingHandler.class.getName();
    private final SparseBooleanArray openedPages = new SparseBooleanArray();
    private PdfDocument pdfDocument;
    private PDFView pdfView;
    private PdfiumCore pdfiumCore;
    private RectF renderBounds = new RectF();
    private Matrix renderMatrix = new Matrix();
    private Rect roundedRenderBounds = new Rect();
    private boolean running = false;

    RenderingHandler(Looper looper, PDFView pDFView, PdfiumCore pdfiumCore, PdfDocument pdfDocument) {
        super(looper);
        this.pdfView = pDFView;
        this.pdfiumCore = pdfiumCore;
        this.pdfDocument = pdfDocument;
    }

    private void calculateBounds(int n, int n2, RectF rectF) {
        this.renderMatrix.reset();
        this.renderMatrix.postTranslate(-rectF.left * (float)n, -rectF.top * (float)n2);
        this.renderMatrix.postScale(1.0f / rectF.width(), 1.0f / rectF.height());
        this.renderBounds.set(0.0f, 0.0f, (float)n, (float)n2);
        this.renderMatrix.mapRect(this.renderBounds);
        this.renderBounds.round(this.roundedRenderBounds);
    }

    private PagePart proceed(RenderingTask renderingTask) throws PageRenderingException {
        Bitmap bitmap;
        if (this.openedPages.indexOfKey(renderingTask.page) < 0) {
            try {
                this.pdfiumCore.openPage(this.pdfDocument, renderingTask.page);
                this.openedPages.put(renderingTask.page, true);
            }
            catch (Exception exception) {
                this.openedPages.put(renderingTask.page, false);
                throw new PageRenderingException(renderingTask.page, exception);
            }
        }
        int n = Math.round((float)renderingTask.width);
        int n2 = Math.round((float)renderingTask.height);
        try {
            Bitmap.Config config = renderingTask.bestQuality ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap((int)n, (int)n2, (Bitmap.Config)config);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
            return null;
        }
        this.calculateBounds(n, n2, renderingTask.bounds);
        if (this.openedPages.get(renderingTask.page)) {
            this.pdfiumCore.renderPageBitmap(this.pdfDocument, bitmap, renderingTask.page, this.roundedRenderBounds.left, this.roundedRenderBounds.top, this.roundedRenderBounds.width(), this.roundedRenderBounds.height(), renderingTask.annotationRendering);
        } else {
            bitmap.eraseColor(this.pdfView.getInvalidPageColor());
        }
        PagePart pagePart = new PagePart(renderingTask.userPage, renderingTask.page, bitmap, renderingTask.width, renderingTask.height, renderingTask.bounds, renderingTask.thumbnail, renderingTask.cacheOrder);
        return pagePart;
    }

    void addRenderingTask(int n, int n2, float f, float f2, RectF rectF, boolean bl, int n3, boolean bl2, boolean bl3) {
        RenderingTask renderingTask = new RenderingTask(f, f2, rectF, n, n2, bl, n3, bl2, bl3);
        this.sendMessage(this.obtainMessage(1, (Object)renderingTask));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void handleMessage(Message message) {
        PagePart pagePart;
        RenderingTask renderingTask = (RenderingTask)message.obj;
        try {
            pagePart = this.proceed(renderingTask);
            if (pagePart == null) return;
        }
        catch (PageRenderingException pageRenderingException) {
            this.pdfView.post(new Runnable(){

                public void run() {
                    RenderingHandler.this.pdfView.onPageError(pageRenderingException);
                }
            });
            return;
        }
        if (this.running) {
            this.pdfView.post(new Runnable(){

                public void run() {
                    RenderingHandler.this.pdfView.onBitmapRendered(pagePart);
                }
            });
            return;
        } else {
            pagePart.getRenderedBitmap().recycle();
        }
    }

    void start() {
        this.running = true;
    }

    void stop() {
        this.running = false;
    }

    private class RenderingTask {
        boolean annotationRendering;
        boolean bestQuality;
        RectF bounds;
        int cacheOrder;
        float height;
        int page;
        boolean thumbnail;
        int userPage;
        float width;

        RenderingTask(float f, float f2, RectF rectF, int n, int n2, boolean bl, int n3, boolean bl2, boolean bl3) {
            this.page = n2;
            this.width = f;
            this.height = f2;
            this.bounds = rectF;
            this.userPage = n;
            this.thumbnail = bl;
            this.cacheOrder = n3;
            this.bestQuality = bl2;
            this.annotationRendering = bl3;
        }
    }

}

