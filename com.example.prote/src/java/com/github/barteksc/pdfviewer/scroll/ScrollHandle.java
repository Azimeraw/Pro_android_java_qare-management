/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.github.barteksc.pdfviewer.scroll;

import com.github.barteksc.pdfviewer.PDFView;

public interface ScrollHandle {
    public void destroyLayout();

    public void hide();

    public void hideDelayed();

    public void setPageNum(int var1);

    public void setScroll(float var1);

    public void setupLayout(PDFView var1);

    public void show();

    public boolean shown();
}

