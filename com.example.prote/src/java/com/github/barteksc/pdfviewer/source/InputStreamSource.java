/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.String
 */
package com.github.barteksc.pdfviewer.source;

import android.content.Context;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.github.barteksc.pdfviewer.util.Util;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamSource
implements DocumentSource {
    private InputStream inputStream;

    public InputStreamSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore pdfiumCore, String string2) throws IOException {
        return pdfiumCore.newDocument(Util.toByteArray(this.inputStream), string2);
    }
}

