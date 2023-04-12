/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 */
package com.github.barteksc.pdfviewer.exception;

@Deprecated
public class FileNotFoundException
extends RuntimeException {
    public FileNotFoundException(String string2) {
        super(string2);
    }

    public FileNotFoundException(String string2, Throwable throwable) {
        super(string2, throwable);
    }
}

