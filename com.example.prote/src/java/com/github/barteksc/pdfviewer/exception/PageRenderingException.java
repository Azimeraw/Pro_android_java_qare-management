/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Throwable
 */
package com.github.barteksc.pdfviewer.exception;

public class PageRenderingException
extends Exception {
    private final int page;

    public PageRenderingException(int n, Throwable throwable) {
        super(throwable);
        this.page = n;
    }

    public int getPage() {
        return this.page;
    }
}

