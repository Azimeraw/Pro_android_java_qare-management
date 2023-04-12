/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.animation.Animator
 *  android.animation.Animator$AnimatorListener
 *  android.animation.AnimatorListenerAdapter
 *  android.animation.TimeInterpolator
 *  android.animation.ValueAnimator
 *  android.animation.ValueAnimator$AnimatorUpdateListener
 *  android.content.Context
 *  android.graphics.PointF
 *  android.view.animation.DecelerateInterpolator
 *  android.widget.OverScroller
 *  java.lang.Float
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;

class AnimationManager {
    private ValueAnimator animation;
    private boolean flinging = false;
    private PDFView pdfView;
    private OverScroller scroller;

    public AnimationManager(PDFView pDFView) {
        this.pdfView = pDFView;
        this.scroller = new OverScroller(pDFView.getContext());
    }

    private void hideHandle() {
        if (this.pdfView.getScrollHandle() != null) {
            this.pdfView.getScrollHandle().hideDelayed();
        }
    }

    void computeFling() {
        if (this.scroller.computeScrollOffset()) {
            this.pdfView.moveTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            this.pdfView.loadPageByOffset();
            return;
        }
        if (this.flinging) {
            this.flinging = false;
            this.pdfView.loadPages();
            this.hideHandle();
        }
    }

    public void startFlingAnimation(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.stopAll();
        this.flinging = true;
        this.scroller.fling(n, n2, n3, n4, n5, n6, n7, n8);
    }

    public void startXAnimation(float f, float f2) {
        this.stopAll();
        this.animation = ValueAnimator.ofFloat((float[])new float[]{f, f2});
        XAnimation xAnimation = new XAnimation();
        this.animation.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
        this.animation.addUpdateListener((ValueAnimator.AnimatorUpdateListener)xAnimation);
        this.animation.addListener((Animator.AnimatorListener)xAnimation);
        this.animation.setDuration(400L);
        this.animation.start();
    }

    public void startYAnimation(float f, float f2) {
        this.stopAll();
        this.animation = ValueAnimator.ofFloat((float[])new float[]{f, f2});
        YAnimation yAnimation = new YAnimation();
        this.animation.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
        this.animation.addUpdateListener((ValueAnimator.AnimatorUpdateListener)yAnimation);
        this.animation.addListener((Animator.AnimatorListener)yAnimation);
        this.animation.setDuration(400L);
        this.animation.start();
    }

    public void startZoomAnimation(float f, float f2, float f3, float f4) {
        ValueAnimator valueAnimator;
        this.stopAll();
        this.animation = valueAnimator = ValueAnimator.ofFloat((float[])new float[]{f3, f4});
        valueAnimator.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
        ZoomAnimation zoomAnimation = new ZoomAnimation(f, f2);
        this.animation.addUpdateListener((ValueAnimator.AnimatorUpdateListener)zoomAnimation);
        this.animation.addListener((Animator.AnimatorListener)zoomAnimation);
        this.animation.setDuration(400L);
        this.animation.start();
    }

    public void stopAll() {
        ValueAnimator valueAnimator = this.animation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.animation = null;
        }
        this.stopFling();
    }

    public void stopFling() {
        this.flinging = false;
        this.scroller.forceFinished(true);
    }

    class XAnimation
    extends AnimatorListenerAdapter
    implements ValueAnimator.AnimatorUpdateListener {
        XAnimation() {
        }

        public void onAnimationCancel(Animator animator2) {
            AnimationManager.this.pdfView.loadPages();
        }

        public void onAnimationEnd(Animator animator2) {
            AnimationManager.this.pdfView.loadPages();
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float f = ((Float)valueAnimator.getAnimatedValue()).floatValue();
            AnimationManager.this.pdfView.moveTo(f, AnimationManager.this.pdfView.getCurrentYOffset());
            AnimationManager.this.pdfView.loadPageByOffset();
        }
    }

    class YAnimation
    extends AnimatorListenerAdapter
    implements ValueAnimator.AnimatorUpdateListener {
        YAnimation() {
        }

        public void onAnimationCancel(Animator animator2) {
            AnimationManager.this.pdfView.loadPages();
        }

        public void onAnimationEnd(Animator animator2) {
            AnimationManager.this.pdfView.loadPages();
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float f = ((Float)valueAnimator.getAnimatedValue()).floatValue();
            AnimationManager.this.pdfView.moveTo(AnimationManager.this.pdfView.getCurrentXOffset(), f);
            AnimationManager.this.pdfView.loadPageByOffset();
        }
    }

    class ZoomAnimation
    implements ValueAnimator.AnimatorUpdateListener,
    Animator.AnimatorListener {
        private final float centerX;
        private final float centerY;

        public ZoomAnimation(float f, float f2) {
            this.centerX = f;
            this.centerY = f2;
        }

        public void onAnimationCancel(Animator animator2) {
        }

        public void onAnimationEnd(Animator animator2) {
            AnimationManager.this.pdfView.loadPages();
            AnimationManager.this.hideHandle();
        }

        public void onAnimationRepeat(Animator animator2) {
        }

        public void onAnimationStart(Animator animator2) {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float f = ((Float)valueAnimator.getAnimatedValue()).floatValue();
            AnimationManager.this.pdfView.zoomCenteredTo(f, new PointF(this.centerX, this.centerY));
        }
    }

}

