/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.drawable.Drawable
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Handler
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.RelativeLayout
 *  android.widget.RelativeLayout$LayoutParams
 *  android.widget.TextView
 *  androidx.core.content.ContextCompat
 *  java.lang.CharSequence
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 */
package com.github.barteksc.pdfviewer.scroll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.R;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.Util;

public class DefaultScrollHandle
extends RelativeLayout
implements ScrollHandle {
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int HANDLE_LONG = 65;
    private static final int HANDLE_SHORT = 40;
    protected Context context;
    private float currentPos;
    private Handler handler = new Handler();
    private Runnable hidePageScrollerRunnable = new Runnable(){

        public void run() {
            DefaultScrollHandle.this.hide();
        }
    };
    private boolean inverted;
    private PDFView pdfView;
    private float relativeHandlerMiddle = 0.0f;
    protected TextView textView;

    public DefaultScrollHandle(Context context) {
        this(context, false);
    }

    public DefaultScrollHandle(Context context, boolean bl) {
        super(context);
        this.context = context;
        this.inverted = bl;
        this.textView = new TextView(context);
        this.setVisibility(4);
        this.setTextColor(-16777216);
        this.setTextSize(16);
    }

    private void calculateMiddle() {
        float f;
        float f2;
        float f3;
        if (this.pdfView.isSwipeVertical()) {
            f2 = this.getY();
            f = this.getHeight();
            f3 = this.pdfView.getHeight();
        } else {
            f2 = this.getX();
            f = this.getWidth();
            f3 = this.pdfView.getWidth();
        }
        this.relativeHandlerMiddle = f * ((f2 + this.relativeHandlerMiddle) / f3);
    }

    private boolean isPDFViewReady() {
        PDFView pDFView = this.pdfView;
        return pDFView != null && pDFView.getPageCount() > 0 && !this.pdfView.documentFitsView();
    }

    private void setPosition(float f) {
        if (!Float.isInfinite((float)f)) {
            if (Float.isNaN((float)f)) {
                return;
            }
            float f2 = this.pdfView.isSwipeVertical() ? (float)this.pdfView.getHeight() : (float)this.pdfView.getWidth();
            float f3 = f - this.relativeHandlerMiddle;
            if (f3 < 0.0f) {
                f3 = 0.0f;
            } else if (f3 > f2 - (float)Util.getDP(this.context, 40)) {
                f3 = f2 - (float)Util.getDP(this.context, 40);
            }
            if (this.pdfView.isSwipeVertical()) {
                this.setY(f3);
            } else {
                this.setX(f3);
            }
            this.calculateMiddle();
            this.invalidate();
            return;
        }
    }

    @Override
    public void destroyLayout() {
        this.pdfView.removeView((View)this);
    }

    @Override
    public void hide() {
        this.setVisibility(4);
    }

    @Override
    public void hideDelayed() {
        this.handler.postDelayed(this.hidePageScrollerRunnable, 1000L);
    }

    /*
     * Exception decompiling
     */
    public boolean onTouchEvent(MotionEvent var1_1) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Statement already marked as first in another block
        // org.benf.cfr.reader.b.a.a.i.b(Op03SimpleStatement.java:414)
        // org.benf.cfr.reader.b.a.a.b.ad.a(Misc.java:226)
        // org.benf.cfr.reader.b.a.a.b.l.a(ConditionalRewriter.java:623)
        // org.benf.cfr.reader.b.a.a.b.l.a(ConditionalRewriter.java:52)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:576)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
        // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
        // org.benf.cfr.reader.b.a(Driver.java:128)
        // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
        // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
        // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
        // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
        // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
        // java.lang.Thread.run(Thread.java:841)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void setPageNum(int n) {
        String string2 = String.valueOf((int)n);
        if (!this.textView.getText().equals((Object)string2)) {
            this.textView.setText((CharSequence)string2);
        }
    }

    @Override
    public void setScroll(float f) {
        if (!this.shown()) {
            this.show();
        } else {
            this.handler.removeCallbacks(this.hidePageScrollerRunnable);
        }
        int n = this.pdfView.isSwipeVertical() ? this.pdfView.getHeight() : this.pdfView.getWidth();
        this.setPosition(f * (float)n);
    }

    public void setTextColor(int n) {
        this.textView.setTextColor(n);
    }

    public void setTextSize(int n) {
        this.textView.setTextSize(1, (float)n);
    }

    @Override
    public void setupLayout(PDFView pDFView) {
        int n;
        int n2;
        int n3;
        Drawable drawable2;
        if (pDFView.isSwipeVertical()) {
            n = 65;
            n2 = 40;
            if (this.inverted) {
                n3 = 9;
                drawable2 = ContextCompat.getDrawable((Context)this.context, (int)R.drawable.default_scroll_handle_left);
            } else {
                n3 = 11;
                drawable2 = ContextCompat.getDrawable((Context)this.context, (int)R.drawable.default_scroll_handle_right);
            }
        } else {
            n = 40;
            n2 = 65;
            if (this.inverted) {
                n3 = 10;
                drawable2 = ContextCompat.getDrawable((Context)this.context, (int)R.drawable.default_scroll_handle_top);
            } else {
                n3 = 12;
                drawable2 = ContextCompat.getDrawable((Context)this.context, (int)R.drawable.default_scroll_handle_bottom);
            }
        }
        if (Build.VERSION.SDK_INT < 16) {
            this.setBackgroundDrawable(drawable2);
        } else {
            this.setBackground(drawable2);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Util.getDP(this.context, n), Util.getDP(this.context, n2));
        layoutParams.setMargins(0, 0, 0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(13, -1);
        this.addView((View)this.textView, (ViewGroup.LayoutParams)layoutParams2);
        layoutParams.addRule(n3);
        pDFView.addView((View)this, (ViewGroup.LayoutParams)layoutParams);
        this.pdfView = pDFView;
    }

    @Override
    public void show() {
        this.setVisibility(0);
    }

    @Override
    public boolean shown() {
        return this.getVisibility() == 0;
    }

}

