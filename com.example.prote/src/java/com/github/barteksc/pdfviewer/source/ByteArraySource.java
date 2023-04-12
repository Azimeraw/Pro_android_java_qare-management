/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.String
 */
package com.github.barteksc.pdfviewer.source;

import android.content.Context;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.IOException;

public class ByteArraySource
implements DocumentSource {
    private byte[] data;

    public ByteArraySource(byte[] arrby) {
        this.data = arrby;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore pdfiumCore, String string2) throws IOException {
        return pdfiumCore.newDocument(this.data, string2);
    }
}

