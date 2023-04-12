/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Bitmap
 *  android.graphics.Canvas
 *  android.graphics.DrawFilter
 *  android.graphics.Paint
 *  android.graphics.Paint$Style
 *  android.graphics.PaintFlagsDrawFilter
 *  android.graphics.PointF
 *  android.graphics.Rect
 *  android.graphics.RectF
 *  android.graphics.drawable.Drawable
 *  android.net.Uri
 *  android.os.AsyncTask
 *  android.os.HandlerThread
 *  android.os.Looper
 *  android.util.AttributeSet
 *  android.util.Log
 *  android.widget.RelativeLayout
 *  java.io.File
 *  java.io.InputStream
 *  java.lang.Enum
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.concurrent.Executor
 */
package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.github.barteksc.pdfviewer.AnimationManager;
import com.github.barteksc.pdfviewer.CacheManager;
import com.github.barteksc.pdfviewer.DecodingAsyncTask;
import com.github.barteksc.pdfviewer.DragPinchManager;
import com.github.barteksc.pdfviewer.PagesLoader;
import com.github.barteksc.pdfviewer.RenderingHandler;
import com.github.barteksc.pdfviewer.exception.PageRenderingException;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.model.PagePart;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.source.AssetSource;
import com.github.barteksc.pdfviewer.source.ByteArraySource;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.github.barteksc.pdfviewer.source.FileSource;
import com.github.barteksc.pdfviewer.source.InputStreamSource;
import com.github.barteksc.pdfviewer.source.UriSource;
import com.github.barteksc.pdfviewer.util.ArrayUtils;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.MathUtils;
import com.github.barteksc.pdfviewer.util.Util;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class PDFView
extends RelativeLayout {
    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final String TAG = PDFView.class.getSimpleName();
    private AnimationManager animationManager;
    private boolean annotationRendering = false;
    private PaintFlagsDrawFilter antialiasFilter = new PaintFlagsDrawFilter(0, 3);
    private boolean bestQuality = false;
    CacheManager cacheManager;
    private int currentFilteredPage;
    private int currentPage;
    private float currentXOffset = 0.0f;
    private float currentYOffset = 0.0f;
    private Paint debugPaint;
    private DecodingAsyncTask decodingAsyncTask;
    private int defaultPage = 0;
    private int documentPageCount;
    private DragPinchManager dragPinchManager;
    private boolean enableAntialiasing = true;
    private int[] filteredUserPageIndexes;
    private int[] filteredUserPages;
    private int invalidPageColor = -1;
    private boolean isScrollHandleInit = false;
    private float maxZoom = 3.0f;
    private float midZoom = 1.75f;
    private float minZoom = 1.0f;
    private OnDrawListener onDrawAllListener;
    private OnDrawListener onDrawListener;
    private List<Integer> onDrawPagesNums = new ArrayList(10);
    private OnErrorListener onErrorListener;
    private OnLoadCompleteListener onLoadCompleteListener;
    private OnPageChangeListener onPageChangeListener;
    private OnPageErrorListener onPageErrorListener;
    private OnPageScrollListener onPageScrollListener;
    private OnRenderListener onRenderListener;
    private OnTapListener onTapListener;
    private float optimalPageHeight;
    private float optimalPageWidth;
    private int[] originalUserPages;
    private int pageHeight;
    private int pageWidth;
    private PagesLoader pagesLoader;
    private Paint paint;
    private PdfDocument pdfDocument;
    private PdfiumCore pdfiumCore;
    private boolean recycled = true;
    private boolean renderDuringScale = false;
    RenderingHandler renderingHandler;
    private final HandlerThread renderingHandlerThread = new HandlerThread("PDF renderer");
    private ScrollDir scrollDir = ScrollDir.NONE;
    private ScrollHandle scrollHandle;
    private int spacingPx = 0;
    private State state = State.DEFAULT;
    private boolean swipeVertical = true;
    private float zoom = 1.0f;

    public PDFView(Context context, AttributeSet attributeSet) {
        Paint paint;
        super(context, attributeSet);
        if (this.isInEditMode()) {
            return;
        }
        this.cacheManager = new CacheManager();
        this.animationManager = new AnimationManager(this);
        this.dragPinchManager = new DragPinchManager(this, this.animationManager);
        this.paint = new Paint();
        this.debugPaint = paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        this.pdfiumCore = new PdfiumCore(context);
        this.setWillNotDraw(false);
    }

    private float calculateCenterOffsetForPage(int n) {
        if (this.swipeVertical) {
            return -((float)n * this.optimalPageHeight + (float)(n * this.spacingPx)) + ((float)(this.getHeight() / 2) - this.optimalPageHeight / 2.0f);
        }
        return -((float)n * this.optimalPageWidth + (float)(n * this.spacingPx)) + ((float)(this.getWidth() / 2) - this.optimalPageWidth / 2.0f);
    }

    private void calculateOptimalWidthAndHeight() {
        if (this.state != State.DEFAULT) {
            if (this.getWidth() == 0) {
                return;
            }
            float f = this.getWidth();
            float f2 = this.getHeight();
            float f3 = (float)this.pageWidth / (float)this.pageHeight;
            float f4 = f;
            float f5 = (float)Math.floor((double)(f / f3));
            if (f5 > f2) {
                f5 = f2;
                f4 = (float)Math.floor((double)(f2 * f3));
            }
            this.optimalPageWidth = f4;
            this.optimalPageHeight = f5;
            return;
        }
    }

    private float calculatePageOffset(int n) {
        if (this.swipeVertical) {
            return this.toCurrentScale((float)n * this.optimalPageHeight + (float)(n * this.spacingPx));
        }
        return this.toCurrentScale((float)n * this.optimalPageWidth + (float)(n * this.spacingPx));
    }

    private int determineValidPageNumberFrom(int n) {
        if (n <= 0) {
            return 0;
        }
        int[] arrn = this.originalUserPages;
        if (arrn != null) {
            if (n >= arrn.length) {
                return -1 + arrn.length;
            }
        } else {
            int n2 = this.documentPageCount;
            if (n >= n2) {
                return n2 - 1;
            }
        }
        return n;
    }

    private void drawPart(Canvas canvas, PagePart pagePart) {
        float f;
        RectF rectF = pagePart.getPageRelativeBounds();
        Bitmap bitmap = pagePart.getRenderedBitmap();
        if (bitmap.isRecycled()) {
            return;
        }
        float f2 = 0.0f;
        if (this.swipeVertical) {
            f2 = this.calculatePageOffset(pagePart.getUserPage());
            f = 0.0f;
        } else {
            f = this.calculatePageOffset(pagePart.getUserPage());
        }
        canvas.translate(f, f2);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float f3 = this.toCurrentScale(rectF.left * this.optimalPageWidth);
        float f4 = this.toCurrentScale(rectF.top * this.optimalPageHeight);
        float f5 = this.toCurrentScale(rectF.width() * this.optimalPageWidth);
        float f6 = this.toCurrentScale(rectF.height() * this.optimalPageHeight);
        RectF rectF2 = new RectF((float)((int)f3), (float)((int)f4), (float)((int)(f3 + f5)), (float)((int)(f4 + f6)));
        float f7 = f + this.currentXOffset;
        float f8 = f2 + this.currentYOffset;
        if (!(f7 + rectF2.left >= (float)this.getWidth() || f7 + rectF2.right <= 0.0f || f8 + rectF2.top >= (float)this.getHeight() || f8 + rectF2.bottom <= 0.0f)) {
            canvas.drawBitmap(bitmap, rect, rectF2, this.paint);
            if (Constants.DEBUG_MODE) {
                Paint paint = this.debugPaint;
                int n = pagePart.getUserPage() % 2 == 0 ? -65536 : -16776961;
                paint.setColor(n);
                canvas.drawRect(rectF2, this.debugPaint);
            }
            canvas.translate(-f, -f2);
            return;
        }
        canvas.translate(-f, -f2);
    }

    private void drawWithListener(Canvas canvas, int n, OnDrawListener onDrawListener) {
        if (onDrawListener != null) {
            float f;
            float f2;
            if (this.swipeVertical) {
                f = this.calculatePageOffset(n);
                f2 = 0.0f;
            } else {
                f = 0.0f;
                f2 = this.calculatePageOffset(n);
            }
            canvas.translate(f2, f);
            onDrawListener.onLayerDrawn(canvas, this.toCurrentScale(this.optimalPageWidth), this.toCurrentScale(this.optimalPageHeight), n);
            canvas.translate(-f2, -f);
        }
    }

    private void load(DocumentSource documentSource, String string2, OnLoadCompleteListener onLoadCompleteListener, OnErrorListener onErrorListener) {
        this.load(documentSource, string2, onLoadCompleteListener, onErrorListener, null);
    }

    private void load(DocumentSource documentSource, String string2, OnLoadCompleteListener onLoadCompleteListener, OnErrorListener onErrorListener, int[] arrn) {
        if (this.recycled) {
            DecodingAsyncTask decodingAsyncTask;
            if (arrn != null) {
                this.originalUserPages = arrn;
                this.filteredUserPages = ArrayUtils.deleteDuplicatedPages(arrn);
                this.filteredUserPageIndexes = ArrayUtils.calculateIndexesInDuplicateArray(this.originalUserPages);
            }
            this.onLoadCompleteListener = onLoadCompleteListener;
            this.onErrorListener = onErrorListener;
            int[] arrn2 = this.originalUserPages;
            int n = 0;
            if (arrn2 != null) {
                n = arrn2[0];
            }
            this.recycled = false;
            this.decodingAsyncTask = decodingAsyncTask = new DecodingAsyncTask(documentSource, string2, this, this.pdfiumCore, n);
            decodingAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])new Void[0]);
            return;
        }
        throw new IllegalStateException("Don't call load on a PDF View without recycling it first.");
    }

    private void setDefaultPage(int n) {
        this.defaultPage = n;
    }

    private void setInvalidPageColor(int n) {
        this.invalidPageColor = n;
    }

    private void setOnDrawAllListener(OnDrawListener onDrawListener) {
        this.onDrawAllListener = onDrawListener;
    }

    private void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    private void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    private void setOnPageErrorListener(OnPageErrorListener onPageErrorListener) {
        this.onPageErrorListener = onPageErrorListener;
    }

    private void setOnPageScrollListener(OnPageScrollListener onPageScrollListener) {
        this.onPageScrollListener = onPageScrollListener;
    }

    private void setOnRenderListener(OnRenderListener onRenderListener) {
        this.onRenderListener = onRenderListener;
    }

    private void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    private void setScrollHandle(ScrollHandle scrollHandle) {
        this.scrollHandle = scrollHandle;
    }

    private void setSpacing(int n) {
        this.spacingPx = Util.getDP(this.getContext(), n);
    }

    float calculateDocLength() {
        int n = this.getPageCount();
        if (this.swipeVertical) {
            return this.toCurrentScale((float)n * this.optimalPageHeight + (float)((n - 1) * this.spacingPx));
        }
        return this.toCurrentScale((float)n * this.optimalPageWidth + (float)((n - 1) * this.spacingPx));
    }

    public boolean canScrollHorizontally(int n) {
        if (this.swipeVertical) {
            if (n < 0 && this.currentXOffset < 0.0f) {
                return true;
            }
            if (n > 0 && this.currentXOffset + this.toCurrentScale(this.optimalPageWidth) > (float)this.getWidth()) {
                return true;
            }
        } else {
            if (n < 0 && this.currentXOffset < 0.0f) {
                return true;
            }
            if (n > 0 && this.currentXOffset + this.calculateDocLength() > (float)this.getWidth()) {
                return true;
            }
        }
        return false;
    }

    public boolean canScrollVertically(int n) {
        if (this.swipeVertical) {
            if (n < 0 && this.currentYOffset < 0.0f) {
                return true;
            }
            if (n > 0 && this.currentYOffset + this.calculateDocLength() > (float)this.getHeight()) {
                return true;
            }
        } else {
            if (n < 0 && this.currentYOffset < 0.0f) {
                return true;
            }
            if (n > 0 && this.currentYOffset + this.toCurrentScale(this.optimalPageHeight) > (float)this.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.isInEditMode()) {
            return;
        }
        this.animationManager.computeFling();
    }

    public boolean doRenderDuringScale() {
        return this.renderDuringScale;
    }

    public boolean documentFitsView() {
        int n = this.getPageCount();
        int n2 = (n - 1) * this.spacingPx;
        if (this.swipeVertical) {
            float f = (float)n * this.optimalPageHeight + (float)n2 FCMPG (float)this.getHeight();
            boolean bl = false;
            if (f < 0) {
                bl = true;
            }
            return bl;
        }
        float f = (float)n * this.optimalPageWidth + (float)n2 FCMPG (float)this.getWidth();
        boolean bl = false;
        if (f < 0) {
            bl = true;
        }
        return bl;
    }

    public void enableAnnotationRendering(boolean bl) {
        this.annotationRendering = bl;
    }

    public void enableAntialiasing(boolean bl) {
        this.enableAntialiasing = bl;
    }

    public void enableDoubletap(boolean bl) {
        this.dragPinchManager.enableDoubletap(bl);
    }

    public void enableRenderDuringScale(boolean bl) {
        this.renderDuringScale = bl;
    }

    public void enableSwipe(boolean bl) {
        this.dragPinchManager.setSwipeEnabled(bl);
    }

    public void fitToWidth() {
        if (this.state != State.SHOWN) {
            Log.e((String)TAG, (String)"Cannot fit, document not rendered yet");
            return;
        }
        this.zoomTo((float)this.getWidth() / this.optimalPageWidth);
        this.setPositionOffset(0.0f);
    }

    public void fitToWidth(int n) {
        if (this.state != State.SHOWN) {
            Log.e((String)TAG, (String)"Cannot fit, document not rendered yet");
            return;
        }
        this.fitToWidth();
        this.jumpTo(n);
    }

    public Configurator fromAsset(String string2) {
        return new Configurator(new AssetSource(string2));
    }

    public Configurator fromBytes(byte[] arrby) {
        return new Configurator(new ByteArraySource(arrby));
    }

    public Configurator fromFile(File file) {
        return new Configurator(new FileSource(file));
    }

    public Configurator fromSource(DocumentSource documentSource) {
        return new Configurator(documentSource);
    }

    public Configurator fromStream(InputStream inputStream) {
        return new Configurator(new InputStreamSource(inputStream));
    }

    public Configurator fromUri(Uri uri) {
        return new Configurator(new UriSource(uri));
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public float getCurrentXOffset() {
        return this.currentXOffset;
    }

    public float getCurrentYOffset() {
        return this.currentYOffset;
    }

    public PdfDocument.Meta getDocumentMeta() {
        PdfDocument pdfDocument = this.pdfDocument;
        if (pdfDocument == null) {
            return null;
        }
        return this.pdfiumCore.getDocumentMeta(pdfDocument);
    }

    int getDocumentPageCount() {
        return this.documentPageCount;
    }

    int[] getFilteredUserPageIndexes() {
        return this.filteredUserPageIndexes;
    }

    int[] getFilteredUserPages() {
        return this.filteredUserPages;
    }

    public int getInvalidPageColor() {
        return this.invalidPageColor;
    }

    public float getMaxZoom() {
        return this.maxZoom;
    }

    public float getMidZoom() {
        return this.midZoom;
    }

    public float getMinZoom() {
        return this.minZoom;
    }

    OnPageChangeListener getOnPageChangeListener() {
        return this.onPageChangeListener;
    }

    OnPageScrollListener getOnPageScrollListener() {
        return this.onPageScrollListener;
    }

    OnRenderListener getOnRenderListener() {
        return this.onRenderListener;
    }

    OnTapListener getOnTapListener() {
        return this.onTapListener;
    }

    public float getOptimalPageHeight() {
        return this.optimalPageHeight;
    }

    public float getOptimalPageWidth() {
        return this.optimalPageWidth;
    }

    int[] getOriginalUserPages() {
        return this.originalUserPages;
    }

    public int getPageAtPositionOffset(float f) {
        int n = (int)Math.floor((double)(f * (float)this.getPageCount()));
        if (n == this.getPageCount()) {
            return n - 1;
        }
        return n;
    }

    public int getPageCount() {
        int[] arrn = this.originalUserPages;
        if (arrn != null) {
            return arrn.length;
        }
        return this.documentPageCount;
    }

    public float getPositionOffset() {
        float f = this.swipeVertical ? -this.currentYOffset / (this.calculateDocLength() - (float)this.getHeight()) : -this.currentXOffset / (this.calculateDocLength() - (float)this.getWidth());
        return MathUtils.limit(f, 0.0f, 1.0f);
    }

    ScrollDir getScrollDir() {
        return this.scrollDir;
    }

    ScrollHandle getScrollHandle() {
        return this.scrollHandle;
    }

    int getSpacingPx() {
        return this.spacingPx;
    }

    public List<PdfDocument.Bookmark> getTableOfContents() {
        PdfDocument pdfDocument = this.pdfDocument;
        if (pdfDocument == null) {
            return new ArrayList();
        }
        return this.pdfiumCore.getTableOfContents(pdfDocument);
    }

    public float getZoom() {
        return this.zoom;
    }

    public boolean isAnnotationRendering() {
        return this.annotationRendering;
    }

    public boolean isAntialiasing() {
        return this.enableAntialiasing;
    }

    public boolean isBestQuality() {
        return this.bestQuality;
    }

    public boolean isRecycled() {
        return this.recycled;
    }

    public boolean isSwipeVertical() {
        return this.swipeVertical;
    }

    public boolean isZooming() {
        return this.zoom != this.minZoom;
    }

    public void jumpTo(int n) {
        this.jumpTo(n, false);
    }

    public void jumpTo(int n, boolean bl) {
        float f = -this.calculatePageOffset(n);
        if (this.swipeVertical) {
            if (bl) {
                this.animationManager.startYAnimation(this.currentYOffset, f);
            } else {
                this.moveTo(this.currentXOffset, f);
            }
        } else if (bl) {
            this.animationManager.startXAnimation(this.currentXOffset, f);
        } else {
            this.moveTo(f, this.currentYOffset);
        }
        this.showPage(n);
    }

    void loadComplete(PdfDocument pdfDocument, int n, int n2) {
        OnLoadCompleteListener onLoadCompleteListener;
        RenderingHandler renderingHandler;
        this.state = State.LOADED;
        this.documentPageCount = this.pdfiumCore.getPageCount(pdfDocument);
        this.pdfDocument = pdfDocument;
        this.pageWidth = n;
        this.pageHeight = n2;
        this.calculateOptimalWidthAndHeight();
        this.pagesLoader = new PagesLoader(this);
        if (!this.renderingHandlerThread.isAlive()) {
            this.renderingHandlerThread.start();
        }
        this.renderingHandler = renderingHandler = new RenderingHandler(this.renderingHandlerThread.getLooper(), this, this.pdfiumCore, pdfDocument);
        renderingHandler.start();
        ScrollHandle scrollHandle = this.scrollHandle;
        if (scrollHandle != null) {
            scrollHandle.setupLayout(this);
            this.isScrollHandleInit = true;
        }
        if ((onLoadCompleteListener = this.onLoadCompleteListener) != null) {
            onLoadCompleteListener.loadComplete(this.documentPageCount);
        }
        this.jumpTo(this.defaultPage, false);
    }

    void loadError(Throwable throwable) {
        this.state = State.ERROR;
        this.recycle();
        this.invalidate();
        OnErrorListener onErrorListener = this.onErrorListener;
        if (onErrorListener != null) {
            onErrorListener.onError(throwable);
            return;
        }
        Log.e((String)"PDFView", (String)"load pdf error", (Throwable)throwable);
    }

    void loadPageByOffset() {
        float f;
        float f2;
        float f3;
        if (this.getPageCount() == 0) {
            return;
        }
        int n = this.spacingPx;
        float f4 = n - n / this.getPageCount();
        if (this.swipeVertical) {
            f = this.currentYOffset;
            f2 = f4 + this.optimalPageHeight;
            f3 = (float)this.getHeight() / 2.0f;
        } else {
            f = this.currentXOffset;
            f2 = f4 + this.optimalPageWidth;
            f3 = (float)this.getWidth() / 2.0f;
        }
        int n2 = (int)Math.floor((double)((f3 + Math.abs((float)f)) / this.toCurrentScale(f2)));
        if (n2 >= 0 && n2 <= -1 + this.getPageCount() && n2 != this.getCurrentPage()) {
            this.showPage(n2);
            return;
        }
        this.loadPages();
    }

    public void loadPages() {
        if (this.optimalPageWidth != 0.0f && this.optimalPageHeight != 0.0f) {
            RenderingHandler renderingHandler = this.renderingHandler;
            if (renderingHandler == null) {
                return;
            }
            renderingHandler.removeMessages(1);
            this.cacheManager.makeANewSet();
            this.pagesLoader.loadPages();
            this.redraw();
            return;
        }
    }

    public void moveRelativeTo(float f, float f2) {
        this.moveTo(f + this.currentXOffset, f2 + this.currentYOffset);
    }

    public void moveTo(float f, float f2) {
        this.moveTo(f, f2, true);
    }

    public void moveTo(float f, float f2, boolean bl) {
        OnPageScrollListener onPageScrollListener;
        if (this.swipeVertical) {
            float f3 = this.toCurrentScale(this.optimalPageWidth);
            if (f3 < (float)this.getWidth()) {
                f = (float)(this.getWidth() / 2) - f3 / 2.0f;
            } else if (f > 0.0f) {
                f = 0.0f;
            } else if (f + f3 < (float)this.getWidth()) {
                f = (float)this.getWidth() - f3;
            }
            float f4 = this.calculateDocLength();
            if (f4 < (float)this.getHeight()) {
                f2 = ((float)this.getHeight() - f4) / 2.0f;
            } else if (f2 > 0.0f) {
                f2 = 0.0f;
            } else if (f2 + f4 < (float)this.getHeight()) {
                f2 = -f4 + (float)this.getHeight();
            }
            float f5 = this.currentYOffset;
            this.scrollDir = f2 < f5 ? ScrollDir.END : (f2 > f5 ? ScrollDir.START : ScrollDir.NONE);
        } else {
            float f6 = this.toCurrentScale(this.optimalPageHeight);
            if (f6 < (float)this.getHeight()) {
                f2 = (float)(this.getHeight() / 2) - f6 / 2.0f;
            } else if (f2 > 0.0f) {
                f2 = 0.0f;
            } else if (f2 + f6 < (float)this.getHeight()) {
                f2 = (float)this.getHeight() - f6;
            }
            float f7 = this.calculateDocLength();
            if (f7 < (float)this.getWidth()) {
                f = ((float)this.getWidth() - f7) / 2.0f;
            } else if (f > 0.0f) {
                f = 0.0f;
            } else if (f + f7 < (float)this.getWidth()) {
                f = -f7 + (float)this.getWidth();
            }
            float f8 = this.currentXOffset;
            this.scrollDir = f < f8 ? ScrollDir.END : (f > f8 ? ScrollDir.START : ScrollDir.NONE);
        }
        this.currentXOffset = f;
        this.currentYOffset = f2;
        float f9 = this.getPositionOffset();
        if (bl && this.scrollHandle != null && !this.documentFitsView()) {
            this.scrollHandle.setScroll(f9);
        }
        if ((onPageScrollListener = this.onPageScrollListener) != null) {
            onPageScrollListener.onPageScrolled(this.getCurrentPage(), f9);
        }
        this.redraw();
    }

    public void onBitmapRendered(PagePart pagePart) {
        if (this.state == State.LOADED) {
            this.state = State.SHOWN;
            OnRenderListener onRenderListener = this.onRenderListener;
            if (onRenderListener != null) {
                onRenderListener.onInitiallyRendered(this.getPageCount(), this.optimalPageWidth, this.optimalPageHeight);
            }
        }
        if (pagePart.isThumbnail()) {
            this.cacheManager.cacheThumbnail(pagePart);
        } else {
            this.cacheManager.cachePart(pagePart);
        }
        this.redraw();
    }

    protected void onDetachedFromWindow() {
        this.recycle();
        super.onDetachedFromWindow();
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable2;
        if (this.isInEditMode()) {
            return;
        }
        if (this.enableAntialiasing) {
            canvas.setDrawFilter((DrawFilter)this.antialiasFilter);
        }
        if ((drawable2 = this.getBackground()) == null) {
            canvas.drawColor(-1);
        } else {
            drawable2.draw(canvas);
        }
        if (this.recycled) {
            return;
        }
        if (this.state != State.SHOWN) {
            return;
        }
        float f = this.currentXOffset;
        float f2 = this.currentYOffset;
        canvas.translate(f, f2);
        Iterator iterator = this.cacheManager.getThumbnails().iterator();
        while (iterator.hasNext()) {
            this.drawPart(canvas, (PagePart)iterator.next());
        }
        for (PagePart pagePart : this.cacheManager.getPageParts()) {
            this.drawPart(canvas, pagePart);
            if (this.onDrawAllListener == null || this.onDrawPagesNums.contains((Object)pagePart.getUserPage())) continue;
            this.onDrawPagesNums.add((Object)pagePart.getUserPage());
        }
        Iterator iterator2 = this.onDrawPagesNums.iterator();
        while (iterator2.hasNext()) {
            this.drawWithListener(canvas, (Integer)iterator2.next(), this.onDrawAllListener);
        }
        this.onDrawPagesNums.clear();
        this.drawWithListener(canvas, this.currentPage, this.onDrawListener);
        canvas.translate(-f, -f2);
    }

    void onPageError(PageRenderingException pageRenderingException) {
        OnPageErrorListener onPageErrorListener = this.onPageErrorListener;
        if (onPageErrorListener != null) {
            onPageErrorListener.onPageError(pageRenderingException.getPage(), pageRenderingException.getCause());
            return;
        }
        String string2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot open page ");
        stringBuilder.append(pageRenderingException.getPage());
        Log.e((String)string2, (String)stringBuilder.toString(), (Throwable)pageRenderingException.getCause());
    }

    protected void onSizeChanged(int n, int n2, int n3, int n4) {
        if (!this.isInEditMode()) {
            if (this.state != State.SHOWN) {
                return;
            }
            this.animationManager.stopAll();
            this.calculateOptimalWidthAndHeight();
            if (this.swipeVertical) {
                this.moveTo(this.currentXOffset, -this.calculatePageOffset(this.currentPage));
            } else {
                this.moveTo(-this.calculatePageOffset(this.currentPage), this.currentYOffset);
            }
            this.loadPageByOffset();
            return;
        }
    }

    public void recycle() {
        DecodingAsyncTask decodingAsyncTask;
        PdfiumCore pdfiumCore;
        PdfDocument pdfDocument;
        this.animationManager.stopAll();
        RenderingHandler renderingHandler = this.renderingHandler;
        if (renderingHandler != null) {
            renderingHandler.stop();
            this.renderingHandler.removeMessages(1);
        }
        if ((decodingAsyncTask = this.decodingAsyncTask) != null) {
            decodingAsyncTask.cancel(true);
        }
        this.cacheManager.recycle();
        ScrollHandle scrollHandle = this.scrollHandle;
        if (scrollHandle != null && this.isScrollHandleInit) {
            scrollHandle.destroyLayout();
        }
        if ((pdfiumCore = this.pdfiumCore) != null && (pdfDocument = this.pdfDocument) != null) {
            pdfiumCore.closeDocument(pdfDocument);
        }
        this.renderingHandler = null;
        this.originalUserPages = null;
        this.filteredUserPages = null;
        this.filteredUserPageIndexes = null;
        this.pdfDocument = null;
        this.scrollHandle = null;
        this.isScrollHandleInit = false;
        this.currentYOffset = 0.0f;
        this.currentXOffset = 0.0f;
        this.zoom = 1.0f;
        this.recycled = true;
        this.state = State.DEFAULT;
    }

    void redraw() {
        this.invalidate();
    }

    public void resetZoom() {
        this.zoomTo(this.minZoom);
    }

    public void resetZoomWithAnimation() {
        this.zoomWithAnimation(this.minZoom);
    }

    public void setMaxZoom(float f) {
        this.maxZoom = f;
    }

    public void setMidZoom(float f) {
        this.midZoom = f;
    }

    public void setMinZoom(float f) {
        this.minZoom = f;
    }

    public void setPositionOffset(float f) {
        this.setPositionOffset(f, true);
    }

    public void setPositionOffset(float f, boolean bl) {
        if (this.swipeVertical) {
            this.moveTo(this.currentXOffset, f * (-this.calculateDocLength() + (float)this.getHeight()), bl);
        } else {
            this.moveTo(f * (-this.calculateDocLength() + (float)this.getWidth()), this.currentYOffset, bl);
        }
        this.loadPageByOffset();
    }

    public void setSwipeVertical(boolean bl) {
        this.swipeVertical = bl;
    }

    void showPage(int n) {
        OnPageChangeListener onPageChangeListener;
        int n2;
        if (this.recycled) {
            return;
        }
        this.currentPage = n2 = this.determineValidPageNumberFrom(n);
        this.currentFilteredPage = n2;
        int[] arrn = this.filteredUserPageIndexes;
        if (arrn != null && n2 >= 0 && n2 < arrn.length) {
            this.currentFilteredPage = arrn[n2];
        }
        this.loadPages();
        if (this.scrollHandle != null && !this.documentFitsView()) {
            this.scrollHandle.setPageNum(1 + this.currentPage);
        }
        if ((onPageChangeListener = this.onPageChangeListener) != null) {
            onPageChangeListener.onPageChanged(this.currentPage, this.getPageCount());
        }
    }

    public void stopFling() {
        this.animationManager.stopFling();
    }

    public float toCurrentScale(float f) {
        return f * this.zoom;
    }

    public float toRealScale(float f) {
        return f / this.zoom;
    }

    public void useBestQuality(boolean bl) {
        this.bestQuality = bl;
    }

    public void zoomCenteredRelativeTo(float f, PointF pointF) {
        this.zoomCenteredTo(f * this.zoom, pointF);
    }

    public void zoomCenteredTo(float f, PointF pointF) {
        float f2 = f / this.zoom;
        this.zoomTo(f);
        float f3 = f2 * this.currentXOffset;
        float f4 = f2 * this.currentYOffset;
        this.moveTo(f3 + (pointF.x - f2 * pointF.x), f4 + (pointF.y - f2 * pointF.y));
    }

    public void zoomTo(float f) {
        this.zoom = f;
    }

    public void zoomWithAnimation(float f) {
        this.animationManager.startZoomAnimation(this.getWidth() / 2, this.getHeight() / 2, this.zoom, f);
    }

    public void zoomWithAnimation(float f, float f2, float f3) {
        this.animationManager.startZoomAnimation(f, f2, this.zoom, f3);
    }

    public class Configurator {
        private boolean annotationRendering = false;
        private boolean antialiasing = true;
        private int defaultPage = 0;
        private final DocumentSource documentSource;
        private boolean enableDoubletap = true;
        private boolean enableSwipe = true;
        private int invalidPageColor = -1;
        private OnDrawListener onDrawAllListener;
        private OnDrawListener onDrawListener;
        private OnErrorListener onErrorListener;
        private OnLoadCompleteListener onLoadCompleteListener;
        private OnPageChangeListener onPageChangeListener;
        private OnPageErrorListener onPageErrorListener;
        private OnPageScrollListener onPageScrollListener;
        private OnRenderListener onRenderListener;
        private OnTapListener onTapListener;
        private int[] pageNumbers = null;
        private String password = null;
        private ScrollHandle scrollHandle = null;
        private int spacing = 0;
        private boolean swipeHorizontal = false;

        private Configurator(DocumentSource documentSource) {
            this.documentSource = documentSource;
        }

        public Configurator defaultPage(int n) {
            this.defaultPage = n;
            return this;
        }

        public Configurator enableAnnotationRendering(boolean bl) {
            this.annotationRendering = bl;
            return this;
        }

        public Configurator enableAntialiasing(boolean bl) {
            this.antialiasing = bl;
            return this;
        }

        public Configurator enableDoubletap(boolean bl) {
            this.enableDoubletap = bl;
            return this;
        }

        public Configurator enableSwipe(boolean bl) {
            this.enableSwipe = bl;
            return this;
        }

        public Configurator invalidPageColor(int n) {
            this.invalidPageColor = n;
            return this;
        }

        public void load() {
            PDFView.this.recycle();
            PDFView.this.setOnDrawListener(this.onDrawListener);
            PDFView.this.setOnDrawAllListener(this.onDrawAllListener);
            PDFView.this.setOnPageChangeListener(this.onPageChangeListener);
            PDFView.this.setOnPageScrollListener(this.onPageScrollListener);
            PDFView.this.setOnRenderListener(this.onRenderListener);
            PDFView.this.setOnTapListener(this.onTapListener);
            PDFView.this.setOnPageErrorListener(this.onPageErrorListener);
            PDFView.this.enableSwipe(this.enableSwipe);
            PDFView.this.enableDoubletap(this.enableDoubletap);
            PDFView.this.setDefaultPage(this.defaultPage);
            PDFView.this.setSwipeVertical(true ^ this.swipeHorizontal);
            PDFView.this.enableAnnotationRendering(this.annotationRendering);
            PDFView.this.setScrollHandle(this.scrollHandle);
            PDFView.this.enableAntialiasing(this.antialiasing);
            PDFView.this.setSpacing(this.spacing);
            PDFView.this.setInvalidPageColor(this.invalidPageColor);
            PDFView.this.dragPinchManager.setSwipeVertical(PDFView.this.swipeVertical);
            PDFView.this.post(new Runnable(){

                public void run() {
                    if (Configurator.this.pageNumbers != null) {
                        PDFView.this.load(Configurator.this.documentSource, Configurator.this.password, Configurator.this.onLoadCompleteListener, Configurator.this.onErrorListener, Configurator.this.pageNumbers);
                        return;
                    }
                    PDFView.this.load(Configurator.this.documentSource, Configurator.this.password, Configurator.this.onLoadCompleteListener, Configurator.this.onErrorListener);
                }
            });
        }

        public Configurator onDraw(OnDrawListener onDrawListener) {
            this.onDrawListener = onDrawListener;
            return this;
        }

        public Configurator onDrawAll(OnDrawListener onDrawListener) {
            this.onDrawAllListener = onDrawListener;
            return this;
        }

        public Configurator onError(OnErrorListener onErrorListener) {
            this.onErrorListener = onErrorListener;
            return this;
        }

        public Configurator onLoad(OnLoadCompleteListener onLoadCompleteListener) {
            this.onLoadCompleteListener = onLoadCompleteListener;
            return this;
        }

        public Configurator onPageChange(OnPageChangeListener onPageChangeListener) {
            this.onPageChangeListener = onPageChangeListener;
            return this;
        }

        public Configurator onPageError(OnPageErrorListener onPageErrorListener) {
            this.onPageErrorListener = onPageErrorListener;
            return this;
        }

        public Configurator onPageScroll(OnPageScrollListener onPageScrollListener) {
            this.onPageScrollListener = onPageScrollListener;
            return this;
        }

        public Configurator onRender(OnRenderListener onRenderListener) {
            this.onRenderListener = onRenderListener;
            return this;
        }

        public Configurator onTap(OnTapListener onTapListener) {
            this.onTapListener = onTapListener;
            return this;
        }

        public /* varargs */ Configurator pages(int ... arrn) {
            this.pageNumbers = arrn;
            return this;
        }

        public Configurator password(String string2) {
            this.password = string2;
            return this;
        }

        public Configurator scrollHandle(ScrollHandle scrollHandle) {
            this.scrollHandle = scrollHandle;
            return this;
        }

        public Configurator spacing(int n) {
            this.spacing = n;
            return this;
        }

        public Configurator swipeHorizontal(boolean bl) {
            this.swipeHorizontal = bl;
            return this;
        }

    }

    static final class ScrollDir
    extends Enum<ScrollDir> {
        private static final /* synthetic */ ScrollDir[] $VALUES;
        public static final /* enum */ ScrollDir END;
        public static final /* enum */ ScrollDir NONE;
        public static final /* enum */ ScrollDir START;

        static {
            ScrollDir scrollDir;
            NONE = new ScrollDir();
            START = new ScrollDir();
            END = scrollDir = new ScrollDir();
            ScrollDir[] arrscrollDir = new ScrollDir[]{NONE, START, scrollDir};
            $VALUES = arrscrollDir;
        }

        public static ScrollDir valueOf(String string2) {
            return (ScrollDir)Enum.valueOf(ScrollDir.class, (String)string2);
        }

        public static ScrollDir[] values() {
            return (ScrollDir[])$VALUES.clone();
        }
    }

    private static final class State
    extends Enum<State> {
        private static final /* synthetic */ State[] $VALUES;
        public static final /* enum */ State DEFAULT;
        public static final /* enum */ State ERROR;
        public static final /* enum */ State LOADED;
        public static final /* enum */ State SHOWN;

        static {
            State state;
            DEFAULT = new State();
            LOADED = new State();
            SHOWN = new State();
            ERROR = state = new State();
            State[] arrstate = new State[]{DEFAULT, LOADED, SHOWN, state};
            $VALUES = arrstate;
        }

        public static State valueOf(String string2) {
            return (State)Enum.valueOf(State.class, (String)string2);
        }

        public static State[] values() {
            return (State[])$VALUES.clone();
        }
    }

}

