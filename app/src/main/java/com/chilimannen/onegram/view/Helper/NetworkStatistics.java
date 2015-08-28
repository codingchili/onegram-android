package com.chilimannen.onegram.view.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chilimannen.onegram.model.API.Query;

import java.text.DecimalFormat;

/**
 * @author Robin Duda
 *
 * Tracks networks statistics.
 */

public class NetworkStatistics implements NetworkStatisticsHandler {
    private NetworkStatisticsDatabase helper;
    private SQLiteDatabase database;

    public NetworkStatistics(Context context) {
        helper = new NetworkStatisticsDatabase(context);
    }

    private void close() {
        helper.close();
    }

    private void open() {
        database = helper.getWritableDatabase();
    }

    private void addUpload(int size) {
        open();
        ContentValues values = new ContentValues();
        values.put(helper.COLUMN_SIZE, size);
        database.insert(helper.TABLE_UPLOAD, null, values);
        close();
    }

    private void addDownload(int size, long ping) {
        open();
        ContentValues values = new ContentValues();
        values.put(helper.COLUMN_SIZE, size);
        values.put(helper.COLUMN_PING, ping);
        database.insert(helper.TABLE_DOWNLOAD, null, values);
        close();
    }

    public String formatBytes(int bytes) {

        if (bytes < 1000)
            return bytes + " bytes";

        if (bytes < 1000 * 1000)
            return new DecimalFormat("#.##").format(bytes / 1000.0) + " kB";

        if (bytes < 1000 * 1000 * 1000)
            return new DecimalFormat("#.##").format((bytes / 1000.0) / 1000.0) + " MB";

        return new DecimalFormat("#.##").format(((bytes / 1000.0) / 1000.0) / 1000.0) + " GB";
    }

    private String getTransferredBytes(String query) {
        int bytes = 0;
        open();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
            bytes = cursor.getInt(0);

        cursor.close();
        close();

        return formatBytes(bytes);
    }

    public void resetStatistics() {
        open();
        helper.reset(database);
        close();
    }

    public String getUploaded() {
        return getTransferredBytes(helper.uploaded_bytes);
    }

    public String getDownloaded() {
        return getTransferredBytes(helper.downloaded_bytes);

    }

    public String getPingAverage() {
        int ms = 0;
        open();
        Cursor cursor = database.rawQuery(helper.ping_average, null);

        if (cursor.moveToFirst())
            ms = cursor.getInt(0);

        cursor.close();
        close();

        return ms + "ms";
    }

    public void addRequest(Query query) {
        addUpload(query.getRequest().getSize());
        addDownload(query.getResponse().getSize(), query.getResponse().getPing());
    }

}
