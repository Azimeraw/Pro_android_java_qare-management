/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.pm.ApplicationInfo
 *  android.content.res.AssetManager
 *  android.database.sqlite.SQLiteDatabase
 *  android.database.sqlite.SQLiteDatabase$CursorFactory
 *  android.database.sqlite.SQLiteException
 *  android.database.sqlite.SQLiteOpenHelper
 *  android.util.Log
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Deprecated
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.List
 *  java.util.zip.ZipInputStream
 */
package com.readystatesoftware.sqliteasset;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.readystatesoftware.sqliteasset.Utils;
import com.readystatesoftware.sqliteasset.VersionComparator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipInputStream;

public class SQLiteAssetHelper
extends SQLiteOpenHelper {
    private static final String ASSET_DB_PATH = "databases";
    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
    private String mAssetPath;
    private final Context mContext;
    private SQLiteDatabase mDatabase = null;
    private String mDatabasePath;
    private final SQLiteDatabase.CursorFactory mFactory;
    private int mForcedUpgradeVersion = 0;
    private boolean mIsInitializing = false;
    private final String mName;
    private final int mNewVersion;
    private String mUpgradePathFormat;

    public SQLiteAssetHelper(Context context, String string2, SQLiteDatabase.CursorFactory cursorFactory, int n) {
        this(context, string2, null, cursorFactory, n);
    }

    public SQLiteAssetHelper(Context context, String string2, String string3, SQLiteDatabase.CursorFactory cursorFactory, int n) {
        super(context, string2, cursorFactory, n);
        if (n >= 1) {
            if (string2 != null) {
                this.mContext = context;
                this.mName = string2;
                this.mFactory = cursorFactory;
                this.mNewVersion = n;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("databases/");
                stringBuilder.append(string2);
                this.mAssetPath = stringBuilder.toString();
                if (string3 != null) {
                    this.mDatabasePath = string3;
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(context.getApplicationInfo().dataDir);
                    stringBuilder2.append("/databases");
                    this.mDatabasePath = stringBuilder2.toString();
                }
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("databases/");
                stringBuilder3.append(string2);
                stringBuilder3.append("_upgrade_%s-%s.sql");
                this.mUpgradePathFormat = stringBuilder3.toString();
                return;
            }
            throw new IllegalArgumentException("Database name cannot be null");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Version must be >= 1, was ");
        stringBuilder.append(n);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void copyDatabaseFromAssets() throws SQLiteAssetException {
        boolean bl;
        String string2;
        InputStream inputStream;
        Log.w((String)TAG, (String)"copying database from assets...");
        String string3 = this.mAssetPath;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mDatabasePath);
        stringBuilder.append("/");
        stringBuilder.append(this.mName);
        string2 = stringBuilder.toString();
        bl = false;
        try {
            inputStream = this.mContext.getAssets().open(string3);
            bl = false;
        }
        catch (IOException iOException) {
            try {
                InputStream inputStream2;
                AssetManager assetManager = this.mContext.getAssets();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(string3);
                stringBuilder2.append(".zip");
                inputStream = inputStream2 = assetManager.open(stringBuilder2.toString());
                bl = true;
            }
            catch (IOException iOException2) {
                try {
                    InputStream inputStream3;
                    AssetManager assetManager = this.mContext.getAssets();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(string3);
                    stringBuilder3.append(".gz");
                    inputStream = inputStream3 = assetManager.open(stringBuilder3.toString());
                }
                catch (IOException iOException3) {
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("Missing ");
                    stringBuilder4.append(this.mAssetPath);
                    stringBuilder4.append(" file (or .zip, .gz archive) in assets, or target folder not writable");
                    SQLiteAssetException sQLiteAssetException = new SQLiteAssetException(stringBuilder4.toString());
                    sQLiteAssetException.setStackTrace(iOException3.getStackTrace());
                    throw sQLiteAssetException;
                }
            }
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mDatabasePath);
            stringBuilder.append("/");
            File file = new File(stringBuilder.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            if (bl) {
                ZipInputStream zipInputStream = Utils.getFileFromZip(inputStream);
                if (zipInputStream == null) {
                    throw new SQLiteAssetException("Archive is missing a SQLite database file");
                }
                Utils.writeExtractedFileToDisk((InputStream)zipInputStream, (OutputStream)new FileOutputStream(string2));
            } else {
                Utils.writeExtractedFileToDisk(inputStream, (OutputStream)new FileOutputStream(string2));
            }
            Log.w((String)TAG, (String)"database copy complete");
            return;
        }
        catch (IOException iOException) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to write ");
            stringBuilder.append(string2);
            stringBuilder.append(" to data directory");
            SQLiteAssetException sQLiteAssetException = new SQLiteAssetException(stringBuilder.toString());
            sQLiteAssetException.setStackTrace(iOException.getStackTrace());
            throw sQLiteAssetException;
        }
    }

    private SQLiteDatabase createOrOpenDatabase(boolean bl) throws SQLiteAssetException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mDatabasePath);
        stringBuilder.append("/");
        stringBuilder.append(this.mName);
        boolean bl2 = new File(stringBuilder.toString()).exists();
        SQLiteDatabase sQLiteDatabase = null;
        if (bl2) {
            sQLiteDatabase = this.returnDatabase();
        }
        if (sQLiteDatabase != null) {
            if (bl) {
                Log.w((String)TAG, (String)"forcing database upgrade!");
                this.copyDatabaseFromAssets();
                sQLiteDatabase = this.returnDatabase();
            }
            return sQLiteDatabase;
        }
        this.copyDatabaseFromAssets();
        return this.returnDatabase();
    }

    private void getUpgradeFilePaths(int n, int n2, int n3, ArrayList<String> arrayList) {
        int n4;
        int n5;
        if (this.getUpgradeSQLStream(n2, n3) != null) {
            String string2 = this.mUpgradePathFormat;
            Object[] arrobject = new Object[]{n2, n3};
            arrayList.add((Object)String.format((String)string2, (Object[])arrobject));
            n4 = n2 - 1;
            n5 = n2;
        } else {
            n4 = n2 - 1;
            n5 = n3;
        }
        if (n4 < n) {
            return;
        }
        this.getUpgradeFilePaths(n, n4, n5, arrayList);
    }

    private InputStream getUpgradeSQLStream(int n, int n2) {
        String string2 = this.mUpgradePathFormat;
        Object[] arrobject = new Object[]{n, n2};
        String string3 = String.format((String)string2, (Object[])arrobject);
        try {
            InputStream inputStream = this.mContext.getAssets().open(string3);
            return inputStream;
        }
        catch (IOException iOException) {
            String string4 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("missing database upgrade script: ");
            stringBuilder.append(string3);
            Log.w((String)string4, (String)stringBuilder.toString());
            return null;
        }
    }

    private SQLiteDatabase returnDatabase() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mDatabasePath);
            stringBuilder.append("/");
            stringBuilder.append(this.mName);
            SQLiteDatabase sQLiteDatabase = SQLiteDatabase.openDatabase((String)stringBuilder.toString(), (SQLiteDatabase.CursorFactory)this.mFactory, (int)0);
            String string2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("successfully opened database ");
            stringBuilder2.append(this.mName);
            Log.i((String)string2, (String)stringBuilder2.toString());
            return sQLiteDatabase;
        }
        catch (SQLiteException sQLiteException) {
            String string3 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("could not open database ");
            stringBuilder.append(this.mName);
            stringBuilder.append(" - ");
            stringBuilder.append(sQLiteException.getMessage());
            Log.w((String)string3, (String)stringBuilder.toString());
            return null;
        }
    }

    public void close() {
        SQLiteAssetHelper sQLiteAssetHelper = this;
        synchronized (sQLiteAssetHelper) {
            if (!this.mIsInitializing) {
                if (this.mDatabase != null && this.mDatabase.isOpen()) {
                    this.mDatabase.close();
                    this.mDatabase = null;
                }
                return;
            }
            throw new IllegalStateException("Closed during initialization");
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public SQLiteDatabase getReadableDatabase() {
        SQLiteAssetHelper sQLiteAssetHelper = this;
        synchronized (sQLiteAssetHelper) {
            if (this.mDatabase != null && this.mDatabase.isOpen()) {
                return this.mDatabase;
            }
            boolean bl = this.mIsInitializing;
            if (bl) {
                throw new IllegalStateException("getReadableDatabase called recursively");
            }
            try {
                return this.getWritableDatabase();
            }
            catch (SQLiteException sQLiteException) {
                if (this.mName == null) {
                    throw sQLiteException;
                }
                String string2 = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't open ");
                stringBuilder.append(this.mName);
                stringBuilder.append(" for writing (will try read-only):");
                Log.e((String)string2, (String)stringBuilder.toString(), (Throwable)sQLiteException);
                SQLiteDatabase sQLiteDatabase = null;
                try {
                    this.mIsInitializing = true;
                    String string3 = this.mContext.getDatabasePath(this.mName).getPath();
                    sQLiteDatabase = SQLiteDatabase.openDatabase((String)string3, (SQLiteDatabase.CursorFactory)this.mFactory, (int)1);
                    if (sQLiteDatabase.getVersion() == this.mNewVersion) {
                        this.onOpen(sQLiteDatabase);
                        String string4 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Opened ");
                        stringBuilder2.append(this.mName);
                        stringBuilder2.append(" in read-only mode");
                        Log.w((String)string4, (String)stringBuilder2.toString());
                        this.mDatabase = sQLiteDatabase;
                        return sQLiteDatabase;
                    }
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Can't upgrade read-only database from version ");
                    stringBuilder3.append(sQLiteDatabase.getVersion());
                    stringBuilder3.append(" to ");
                    stringBuilder3.append(this.mNewVersion);
                    stringBuilder3.append(": ");
                    stringBuilder3.append(string3);
                    throw new SQLiteException(stringBuilder3.toString());
                }
                finally {
                    this.mIsInitializing = false;
                    if (sQLiteDatabase != null && sQLiteDatabase != sQLiteDatabase) {
                        sQLiteDatabase.close();
                    }
                }
            }
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public SQLiteDatabase getWritableDatabase() {
        block21 : {
            var21_1 = this;
            // MONITORENTER : var21_1
            if (this.mDatabase != null && this.mDatabase.isOpen() && !this.mDatabase.isReadOnly()) {
                var20_2 = this.mDatabase;
                // MONITOREXIT : var21_1
                return var20_2;
            }
            var2_3 = this.mIsInitializing;
            if (var2_3 != false) throw new IllegalStateException("getWritableDatabase called recursively");
            var3_4 = null;
            this.mIsInitializing = true;
            var3_4 = this.createOrOpenDatabase(false);
            var7_5 = var3_4.getVersion();
            if (var7_5 != 0 && var7_5 < this.mForcedUpgradeVersion) {
                var3_4 = this.createOrOpenDatabase(true);
                var3_4.setVersion(this.mNewVersion);
                var7_5 = var3_4.getVersion();
            }
            if (var7_5 == this.mNewVersion) ** GOTO lbl46
            var3_4.beginTransaction();
            if (var7_5 != 0) ** GOTO lbl26
            this.onCreate(var3_4);
            break block21;
lbl26: // 1 sources:
            if (var7_5 > this.mNewVersion) {
                var11_6 = SQLiteAssetHelper.TAG;
                var12_7 = new StringBuilder();
                var12_7.append("Can't downgrade read-only database from version ");
                var12_7.append(var7_5);
                var12_7.append(" to ");
                var12_7.append(this.mNewVersion);
                var12_7.append(": ");
                var12_7.append(var3_4.getPath());
                Log.w((String)var11_6, (String)var12_7.toString());
            }
            this.onUpgrade(var3_4, var7_5, this.mNewVersion);
        }
        var3_4.setVersion(this.mNewVersion);
        var3_4.setTransactionSuccessful();
        try {
            block22 : {
                var3_4.endTransaction();
                break block22;
                catch (Throwable var10_8) {
                    var3_4.endTransaction();
                    throw var10_8;
                }
            }
            this.onOpen(var3_4);
            return var3_4;
        }
        finally {
            this.mIsInitializing = false;
            if (true) {
                var8_9 = this.mDatabase;
                if (var8_9 != null) {
                    try {
                        this.mDatabase.close();
                    }
                    catch (Exception var9_10) {}
                }
                this.mDatabase = var3_4;
            } else if (var3_4 != null) {
                var3_4.close();
            }
        }
    }

    public final void onConfigure(SQLiteDatabase sQLiteDatabase) {
    }

    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
    }

    public final void onDowngrade(SQLiteDatabase sQLiteDatabase, int n, int n2) {
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int n, int n2) {
        SQLiteAssetException sQLiteAssetException;
        String string2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Upgrading database ");
        stringBuilder.append(this.mName);
        stringBuilder.append(" from version ");
        stringBuilder.append(n);
        stringBuilder.append(" to ");
        stringBuilder.append(n2);
        stringBuilder.append("...");
        Log.w((String)string2, (String)stringBuilder.toString());
        ArrayList arrayList = new ArrayList();
        this.getUpgradeFilePaths(n, n2 - 1, n2, (ArrayList<String>)arrayList);
        if (!arrayList.isEmpty()) {
            Collections.sort((List)arrayList, (Comparator)new VersionComparator());
            for (String string3 : arrayList) {
                String string4 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("processing upgrade: ");
                stringBuilder2.append(string3);
                Log.w((String)string4, (String)stringBuilder2.toString());
                String string5 = Utils.convertStreamToString(this.mContext.getAssets().open(string3));
                if (string5 == null) continue;
                try {
                    for (String string6 : Utils.splitSqlScript(string5, ';')) {
                        if (string6.trim().length() <= 0) continue;
                        sQLiteDatabase.execSQL(string6);
                    }
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
            String string7 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Successfully upgraded database ");
            stringBuilder3.append(this.mName);
            stringBuilder3.append(" from version ");
            stringBuilder3.append(n);
            stringBuilder3.append(" to ");
            stringBuilder3.append(n2);
            Log.w((String)string7, (String)stringBuilder3.toString());
            return;
        }
        String string8 = TAG;
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("no upgrade script path from ");
        stringBuilder4.append(n);
        stringBuilder4.append(" to ");
        stringBuilder4.append(n2);
        Log.e((String)string8, (String)stringBuilder4.toString());
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append("no upgrade script path from ");
        stringBuilder5.append(n);
        stringBuilder5.append(" to ");
        stringBuilder5.append(n2);
        sQLiteAssetException = new SQLiteAssetException(stringBuilder5.toString());
        throw sQLiteAssetException;
    }

    public void setForcedUpgrade() {
        this.setForcedUpgrade(this.mNewVersion);
    }

    public void setForcedUpgrade(int n) {
        this.mForcedUpgradeVersion = n;
    }

    @Deprecated
    public void setForcedUpgradeVersion(int n) {
        this.setForcedUpgrade(n);
    }

    public static class SQLiteAssetException
    extends SQLiteException {
        public SQLiteAssetException() {
        }

        public SQLiteAssetException(String string2) {
            super(string2);
        }
    }

}

