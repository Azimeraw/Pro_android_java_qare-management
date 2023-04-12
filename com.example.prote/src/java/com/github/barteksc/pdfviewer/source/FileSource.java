/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.ParcelFileDescriptor
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.String
 */
package com.github.barteksc.pdfviewer.source;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.io.IOException;

public class FileSource
implements DocumentSource {
    private File file;

    public FileSource(File file) {
        this.file = file;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore pdfiumCore, String string2) throws IOException {
        return pdfiumCore.newDocument(ParcelFileDescriptor.open((File)this.file, (int)268435456), string2);
    }
}

