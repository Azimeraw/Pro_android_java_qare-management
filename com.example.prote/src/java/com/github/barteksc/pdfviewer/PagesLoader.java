/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.RectF
 *  android.util.Pair
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer;

import android.graphics.RectF;
import android.util.Pair;
import com.github.barteksc.pdfviewer.CacheManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.RenderingHandler;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.MathUtils;

class PagesLoader {
    private int cacheOrder;
    private float colWidth;
    private Pair<Integer, Integer> colsRows;
    private float pageRelativePartHeight;
    private float pageRelativePartWidth;
    private float partRenderHeight;
    private float partRenderWidth;
    private PDFView pdfView;
    private float rowHeight;
    private float scaledHeight;
    private float scaledSpacingPx;
    private float scaledWidth;
    private int thumbnailHeight;
    private final RectF thumbnailRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    private int thumbnailWidth;
    private float xOffset;
    private float yOffset;

    PagesLoader(PDFView pDFView) {
        this.pdfView = pDFView;
    }

    private int documentPage(int n) {
        int n2 = n;
        if (this.pdfView.getOriginalUserPages() != null) {
            if (n >= 0) {
                if (n >= this.pdfView.getOriginalUserPages().length) {
                    return -1;
                }
                n2 = this.pdfView.getOriginalUserPages()[n];
            } else {
                return -1;
            }
        }
        if (n2 >= 0) {
            if (n >= this.pdfView.getDocumentPageCount()) {
                return -1;
            }
            return n2;
        }
        return -1;
    }

    private Holder getPageAndCoordsByOffset(float f, boolean bl) {
        float f2;
        float f3;
        Holder holder = new Holder();
        float f4 = -MathUtils.max(f, 0.0f);
        if (this.pdfView.isSwipeVertical()) {
            holder.page = MathUtils.floor(f4 / (this.scaledHeight + this.scaledSpacingPx));
            f3 = Math.abs((float)(f4 - (this.scaledHeight + this.scaledSpacingPx) * (float)holder.page)) / this.rowHeight;
            f2 = this.xOffset / this.colWidth;
        } else {
            holder.page = MathUtils.floor(f4 / (this.scaledWidth + this.scaledSpacingPx));
            f2 = Math.abs((float)(f4 - (this.scaledWidth + this.scaledSpacingPx) * (float)holder.page)) / this.colWidth;
            f3 = this.yOffset / this.rowHeight;
        }
        if (bl) {
            holder.row = MathUtils.ceil(f3);
            holder.col = MathUtils.ceil(f2);
            return holder;
        }
        holder.row = MathUtils.floor(f3);
        holder.col = MathUtils.floor(f2);
        return holder;
    }

    private Pair<Integer, Integer> getPageColsRows() {
        float f = 1.0f / this.pdfView.getOptimalPageWidth();
        float f2 = 1.0f / this.pdfView.getOptimalPageHeight() * Constants.PART_SIZE / this.pdfView.getZoom();
        float f3 = f * Constants.PART_SIZE / this.pdfView.getZoom();
        int n = MathUtils.ceil(1.0f / f2);
        return new Pair((Object)MathUtils.ceil(1.0f / f3), (Object)n);
    }

    private boolean loadCell(int n, int n2, int n3, int n4, float f, float f2) {
        float f3 = f * (float)n4;
        float f4 = f2 * (float)n3;
        float f5 = f;
        float f6 = f2;
        float f7 = this.partRenderWidth;
        float f8 = this.partRenderHeight;
        if (f3 + f5 > 1.0f) {
            f5 = 1.0f - f3;
        }
        if (f4 + f6 > 1.0f) {
            f6 = 1.0f - f4;
        }
        float f9 = f7 * f5;
        float f10 = f8 * f6;
        RectF rectF = new RectF(f3, f4, f3 + f5, f4 + f6);
        if (f9 > 0.0f && f10 > 0.0f) {
            if (!this.pdfView.cacheManager.upPartIfContained(n, n2, f9, f10, rectF, this.cacheOrder)) {
                this.pdfView.renderingHandler.addRenderingTask(n, n2, f9, f10, rectF, false, this.cacheOrder, this.pdfView.isBestQuality(), this.pdfView.isAnnotationRendering());
            }
            this.cacheOrder = 1 + this.cacheOrder;
            return true;
        }
        return false;
    }

    private int loadRelative(int n, int n2, boolean bl) {
        float f;
        if (this.pdfView.isSwipeVertical()) {
            float f2 = 1.0f + this.rowHeight * (float)n;
            float f3 = this.pdfView.getCurrentYOffset();
            int n3 = bl ? this.pdfView.getHeight() : 0;
            f = f3 - (float)n3 - f2;
        } else {
            float f4 = this.colWidth * (float)n;
            float f5 = this.pdfView.getCurrentXOffset();
            int n4 = bl ? this.pdfView.getWidth() : 0;
            f = f5 - (float)n4 - f4;
        }
        Holder holder = this.getPageAndCoordsByOffset(f, false);
        int n5 = this.documentPage(holder.page);
        if (n5 < 0) {
            return 0;
        }
        this.loadThumbnail(holder.page, n5);
        if (this.pdfView.isSwipeVertical()) {
            int n6 = MathUtils.min(-1 + MathUtils.floor(this.xOffset / this.colWidth), 0);
            int n7 = MathUtils.max(1 + MathUtils.ceil((this.xOffset + (float)this.pdfView.getWidth()) / this.colWidth), (Integer)this.colsRows.first);
            int n8 = 0;
            int n9 = n6;
            while (n9 <= n7) {
                int n10 = holder.page;
                int n11 = holder.row;
                float f6 = this.pageRelativePartWidth;
                float f7 = this.pageRelativePartHeight;
                int n12 = n9;
                int n13 = n9;
                if (this.loadCell(n10, n5, n11, n12, f6, f7)) {
                    ++n8;
                }
                if (n8 >= n2) {
                    return n8;
                }
                n9 = n13 + 1;
            }
            return n8;
        }
        int n14 = MathUtils.min(-1 + MathUtils.floor(this.yOffset / this.rowHeight), 0);
        int n15 = MathUtils.max(1 + MathUtils.ceil((this.yOffset + (float)this.pdfView.getHeight()) / this.rowHeight), (Integer)this.colsRows.second);
        int n16 = 0;
        int n17 = n14;
        while (n17 <= n15) {
            int n18 = holder.page;
            int n19 = holder.col;
            float f8 = this.pageRelativePartWidth;
            float f9 = this.pageRelativePartHeight;
            int n20 = n17;
            int n21 = n17;
            if (this.loadCell(n18, n5, n20, n19, f8, f9)) {
                ++n16;
            }
            if (n16 >= n2) {
                return n16;
            }
            n17 = n21 + 1;
        }
        return n16;
    }

    private void loadThumbnail(int n, int n2) {
        if (!this.pdfView.cacheManager.containsThumbnail(n, n2, this.thumbnailWidth, this.thumbnailHeight, this.thumbnailRect)) {
            this.pdfView.renderingHandler.addRenderingTask(n, n2, this.thumbnailWidth, this.thumbnailHeight, this.thumbnailRect, true, 0, this.pdfView.isBestQuality(), this.pdfView.isAnnotationRendering());
        }
    }

    public void loadPages() {
        float f;
        PDFView pDFView = this.pdfView;
        this.scaledHeight = pDFView.toCurrentScale(pDFView.getOptimalPageHeight());
        PDFView pDFView2 = this.pdfView;
        this.scaledWidth = pDFView2.toCurrentScale(pDFView2.getOptimalPageWidth());
        this.thumbnailWidth = (int)(this.pdfView.getOptimalPageWidth() * Constants.THUMBNAIL_RATIO);
        this.thumbnailHeight = (int)(this.pdfView.getOptimalPageHeight() * Constants.THUMBNAIL_RATIO);
        this.colsRows = this.getPageColsRows();
        this.xOffset = -MathUtils.max(this.pdfView.getCurrentXOffset(), 0.0f);
        this.yOffset = -MathUtils.max(this.pdfView.getCurrentYOffset(), 0.0f);
        this.rowHeight = this.scaledHeight / (float)((Integer)this.colsRows.second).intValue();
        this.colWidth = this.scaledWidth / (float)((Integer)this.colsRows.first).intValue();
        this.pageRelativePartWidth = 1.0f / (float)((Integer)this.colsRows.first).intValue();
        this.pageRelativePartHeight = 1.0f / (float)((Integer)this.colsRows.second).intValue();
        this.partRenderWidth = Constants.PART_SIZE / this.pageRelativePartWidth;
        this.partRenderHeight = Constants.PART_SIZE / this.pageRelativePartHeight;
        this.cacheOrder = 1;
        PDFView pDFView3 = this.pdfView;
        this.scaledSpacingPx = f = pDFView3.toCurrentScale(pDFView3.getSpacingPx());
        this.scaledSpacingPx = f - f / (float)this.pdfView.getPageCount();
        int n = this.loadVisible();
        if (this.pdfView.getScrollDir().equals((Object)PDFView.ScrollDir.END)) {
            for (int i = 0; i < Constants.PRELOAD_COUNT && n < Constants.Cache.CACHE_SIZE; n += this.loadRelative((int)i, (int)n, (boolean)true), ++i) {
            }
        } else {
            for (int i = 0; i > -Constants.PRELOAD_COUNT && n < Constants.Cache.CACHE_SIZE; n += this.loadRelative((int)i, (int)n, (boolean)false), --i) {
            }
        }
    }

    public int loadVisible() {
        int n;
        Holder holder;
        int n2;
        int n3 = 0;
        if (this.pdfView.isSwipeVertical()) {
            int n4;
            holder = this.getPageAndCoordsByOffset(this.pdfView.getCurrentYOffset(), false);
            Holder holder2 = this.getPageAndCoordsByOffset(1.0f + (this.pdfView.getCurrentYOffset() - (float)this.pdfView.getHeight()), true);
            if (holder.page == holder2.page) {
                n4 = 1 + (holder2.row - holder.row);
            } else {
                int n5 = 0 + ((Integer)this.colsRows.second - holder.row);
                for (int i = 1 + holder.page; i < holder2.page; ++i) {
                    n5 += ((Integer)this.colsRows.second).intValue();
                }
                n4 = n5 + (1 + holder2.row);
            }
            for (int i = 0; i < n4 && n3 < Constants.Cache.CACHE_SIZE; n3 += this.loadRelative((int)i, (int)(Constants.Cache.CACHE_SIZE - n3), (boolean)false), ++i) {
            }
        } else {
            int n6;
            holder = this.getPageAndCoordsByOffset(this.pdfView.getCurrentXOffset(), false);
            Holder holder3 = this.getPageAndCoordsByOffset(1.0f + (this.pdfView.getCurrentXOffset() - (float)this.pdfView.getWidth()), true);
            if (holder.page == holder3.page) {
                n6 = 1 + (holder3.col - holder.col);
            } else {
                int n7 = 0 + ((Integer)this.colsRows.first - holder.col);
                for (int i = 1 + holder.page; i < holder3.page; ++i) {
                    n7 += ((Integer)this.colsRows.first).intValue();
                }
                n6 = n7 + (1 + holder3.col);
            }
            for (int i = 0; i < n6 && n3 < Constants.Cache.CACHE_SIZE; n3 += this.loadRelative((int)i, (int)(Constants.Cache.CACHE_SIZE - n3), (boolean)false), ++i) {
            }
        }
        if ((n = this.documentPage(holder.page - 1)) >= 0) {
            this.loadThumbnail(holder.page - 1, n);
        }
        if ((n2 = this.documentPage(1 + holder.page)) >= 0) {
            this.loadThumbnail(1 + holder.page, n2);
        }
        return n3;
    }

    private class Holder {
        int col;
        int page;
        int row;

        private Holder() {
        }
    }

}

