/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.PointF
 *  android.view.GestureDetector
 *  android.view.GestureDetector$OnDoubleTapListener
 *  android.view.GestureDetector$OnGestureListener
 *  android.view.MotionEvent
 *  android.view.ScaleGestureDetector
 *  android.view.ScaleGestureDetector$OnScaleGestureListener
 *  android.view.View
 *  android.view.View$OnTouchListener
 *  java.lang.Math
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.github.barteksc.pdfviewer.AnimationManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.Constants;

class DragPinchManager
implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener,
ScaleGestureDetector.OnScaleGestureListener,
View.OnTouchListener {
    private AnimationManager animationManager;
    private GestureDetector gestureDetector;
    private boolean isSwipeEnabled;
    private PDFView pdfView;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean scaling = false;
    private boolean scrolling = false;
    private boolean swipeVertical;

    public DragPinchManager(PDFView pDFView, AnimationManager animationManager) {
        this.pdfView = pDFView;
        this.animationManager = animationManager;
        this.isSwipeEnabled = false;
        this.swipeVertical = pDFView.isSwipeVertical();
        this.gestureDetector = new GestureDetector(pDFView.getContext(), (GestureDetector.OnGestureListener)this);
        this.scaleGestureDetector = new ScaleGestureDetector(pDFView.getContext(), (ScaleGestureDetector.OnScaleGestureListener)this);
        pDFView.setOnTouchListener((View.OnTouchListener)this);
    }

    private void hideHandle() {
        if (this.pdfView.getScrollHandle() != null && this.pdfView.getScrollHandle().shown()) {
            this.pdfView.getScrollHandle().hideDelayed();
        }
    }

    private boolean isPageChange(float f) {
        float f2;
        PDFView pDFView;
        float f3 = Math.abs((float)f);
        return f3 > Math.abs((float)((pDFView = this.pdfView).toCurrentScale(f2 = this.swipeVertical ? pDFView.getOptimalPageHeight() : pDFView.getOptimalPageWidth()) / 2.0f));
    }

    public void enableDoubletap(boolean bl) {
        if (bl) {
            this.gestureDetector.setOnDoubleTapListener((GestureDetector.OnDoubleTapListener)this);
            return;
        }
        this.gestureDetector.setOnDoubleTapListener(null);
    }

    public boolean isZooming() {
        return this.pdfView.isZooming();
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (this.pdfView.getZoom() < this.pdfView.getMidZoom()) {
            this.pdfView.zoomWithAnimation(motionEvent.getX(), motionEvent.getY(), this.pdfView.getMidZoom());
        } else if (this.pdfView.getZoom() < this.pdfView.getMaxZoom()) {
            this.pdfView.zoomWithAnimation(motionEvent.getX(), motionEvent.getY(), this.pdfView.getMaxZoom());
        } else {
            this.pdfView.resetZoomWithAnimation();
        }
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        this.animationManager.stopFling();
        return true;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float f3;
        float f4;
        int n = (int)this.pdfView.getCurrentXOffset();
        int n2 = (int)this.pdfView.getCurrentYOffset();
        if (this.pdfView.isSwipeVertical()) {
            PDFView pDFView = this.pdfView;
            float f5 = -(pDFView.toCurrentScale(pDFView.getOptimalPageWidth()) - (float)this.pdfView.getWidth());
            float f6 = -(this.pdfView.calculateDocLength() - (float)this.pdfView.getHeight());
            f4 = f5;
            f3 = f6;
        } else {
            float f7 = -(this.pdfView.calculateDocLength() - (float)this.pdfView.getWidth());
            PDFView pDFView = this.pdfView;
            float f8 = -(pDFView.toCurrentScale(pDFView.getOptimalPageHeight()) - (float)this.pdfView.getHeight());
            f4 = f7;
            f3 = f8;
        }
        this.animationManager.startFlingAnimation(n, n2, (int)f, (int)f2, (int)f4, 0, (int)f3, 0);
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float f = scaleGestureDetector.getScaleFactor();
        float f2 = f * this.pdfView.getZoom();
        if (f2 < Constants.Pinch.MINIMUM_ZOOM) {
            f = Constants.Pinch.MINIMUM_ZOOM / this.pdfView.getZoom();
        } else if (f2 > Constants.Pinch.MAXIMUM_ZOOM) {
            f = Constants.Pinch.MAXIMUM_ZOOM / this.pdfView.getZoom();
        }
        this.pdfView.zoomCenteredRelativeTo(f, new PointF(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY()));
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        this.scaling = true;
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        this.pdfView.loadPages();
        this.hideHandle();
        this.scaling = false;
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.scrolling = true;
        if (this.isZooming() || this.isSwipeEnabled) {
            this.pdfView.moveRelativeTo(-f, -f2);
        }
        if (!this.scaling || this.pdfView.doRenderDuringScale()) {
            this.pdfView.loadPageByOffset();
        }
        return true;
    }

    public void onScrollEnd(MotionEvent motionEvent) {
        this.pdfView.loadPages();
        this.hideHandle();
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        ScrollHandle scrollHandle;
        OnTapListener onTapListener = this.pdfView.getOnTapListener();
        if (!(onTapListener != null && onTapListener.onTap(motionEvent) || (scrollHandle = this.pdfView.getScrollHandle()) == null || this.pdfView.documentFitsView())) {
            if (!scrollHandle.shown()) {
                scrollHandle.show();
            } else {
                scrollHandle.hide();
            }
        }
        this.pdfView.performClick();
        return true;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean bl = this.scaleGestureDetector.onTouchEvent(motionEvent);
        boolean bl2 = this.gestureDetector.onTouchEvent(motionEvent) || bl;
        boolean bl3 = bl2;
        if (motionEvent.getAction() == 1 && this.scrolling) {
            this.scrolling = false;
            this.onScrollEnd(motionEvent);
        }
        return bl3;
    }

    public void setSwipeEnabled(boolean bl) {
        this.isSwipeEnabled = bl;
    }

    public void setSwipeVertical(boolean bl) {
        this.swipeVertical = bl;
    }
}

