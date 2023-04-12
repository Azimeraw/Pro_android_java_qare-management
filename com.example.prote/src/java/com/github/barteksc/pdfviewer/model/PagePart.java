/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.Bitmap
 *  android.graphics.RectF
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer.model;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class PagePart {
    private int cacheOrder;
    private float height;
    private int page;
    private RectF pageRelativeBounds;
    private Bitmap renderedBitmap;
    private boolean thumbnail;
    private int userPage;
    private float width;

    public PagePart(int n, int n2, Bitmap bitmap, float f, float f2, RectF rectF, boolean bl, int n3) {
        this.userPage = n;
        this.page = n2;
        this.renderedBitmap = bitmap;
        this.pageRelativeBounds = rectF;
        this.thumbnail = bl;
        this.cacheOrder = n3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PagePart)) {
            return false;
        }
        PagePart pagePart = (PagePart)object;
        return pagePart.getPage() == this.page && pagePart.getUserPage() == this.userPage && pagePart.getWidth() == this.width && pagePart.getHeight() == this.height && pagePart.getPageRelativeBounds().left == this.pageRelativeBounds.left && pagePart.getPageRelativeBounds().right == this.pageRelativeBounds.right && pagePart.getPageRelativeBounds().top == this.pageRelativeBounds.top && pagePart.getPageRelativeBounds().bottom == this.pageRelativeBounds.bottom;
    }

    public int getCacheOrder() {
        return this.cacheOrder;
    }

    public float getHeight() {
        return this.height;
    }

    public int getPage() {
        return this.page;
    }

    public RectF getPageRelativeBounds() {
        return this.pageRelativeBounds;
    }

    public Bitmap getRenderedBitmap() {
        return this.renderedBitmap;
    }

    public int getUserPage() {
        return this.userPage;
    }

    public float getWidth() {
        return this.width;
    }

    public boolean isThumbnail() {
        return this.thumbnail;
    }

    public void setCacheOrder(int n) {
        this.cacheOrder = n;
    }
}

