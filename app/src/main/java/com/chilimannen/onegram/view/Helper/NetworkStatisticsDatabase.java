package com.chilimannen.onegram.view.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Robin Duda
 *
 * Creates and Drops the statistics database.
 */

public class NetworkStatisticsDatabase extends SQLiteOpenHelper {
    public static final String NAME = "network.db";
    public static final int VERSION = 6;

    public final String TABLE_UPLOAD = "upload";
    public final String TABLE_DOWNLOAD = "download";

    public final String COLUMN_SIZE = "size";
    public final String COLUMN_PING = "ping";

    public final String uploaded_bytes = "SELECT sum("+COLUMN_SIZE+") FROM "+TABLE_UPLOAD+"";
    public final String downloaded_bytes = "SELECT sum("+COLUMN_SIZE+") FROM "+TABLE_DOWNLOAD+"";
    public final String ping_average = "SELECT avg("+COLUMN_PING+") FROM "+TABLE_DOWNLOAD+"";;

    private final String CREATE_DOWNLOAD = "" +
            "CREATE TABLE " + TABLE_DOWNLOAD + " (" + COLUMN_SIZE + " integer, " +
            COLUMN_PING + " integer" + ")";

    private final String CREATE_UPLOAD = "" +
            "CREATE TABLE " + TABLE_UPLOAD + " (" + COLUMN_SIZE + ")";

    private final String DROP_DOWNLOAD = "DROP TABLE IF EXISTS " + TABLE_DOWNLOAD;
    private final String DROP_UPLOAD = "DROP TABLE IF EXISTS " + TABLE_UPLOAD;


    public NetworkStatisticsDatabase(Context context) {
        super(context, NAME, null, VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        drop(db);
        create(db);
    }

    public void reset(SQLiteDatabase db) {
        drop(db);
        create(db);
    }

    private void drop(SQLiteDatabase db) {
        db.execSQL(DROP_DOWNLOAD);
        db.execSQL(DROP_UPLOAD);
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(CREATE_DOWNLOAD);
        db.execSQL(CREATE_UPLOAD);
    }
}
