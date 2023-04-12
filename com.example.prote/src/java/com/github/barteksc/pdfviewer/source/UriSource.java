/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.net.Uri
 *  android.os.ParcelFileDescriptor
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.String
 */
package com.github.barteksc.pdfviewer.source;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.IOException;

public class UriSource
implements DocumentSource {
    private Uri uri;

    public UriSource(Uri uri) {
        this.uri = uri;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore pdfiumCore, String string2) throws IOException {
        return pdfiumCore.newDocument(context.getContentResolver().openFileDescriptor(this.uri, "r"), string2);
    }
}

