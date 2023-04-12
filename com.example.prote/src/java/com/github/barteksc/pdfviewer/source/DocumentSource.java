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
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.IOException;

public interface DocumentSource {
    public PdfDocument createDocument(Context var1, PdfiumCore var2, String var3) throws IOException;
}

