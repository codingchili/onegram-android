package com.codingchili.onegram.view.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

/**
 * @author Robin Duda
 *
 * An image in the gallery.
 */

public class GalleryImage {
    private Bitmap bitmap;
    private Date date;
    private String description;
    private String id;
    private boolean loaded = false;
    private boolean loading = false;
    private boolean failed = false;

    public GalleryImage(Long date, String description, String id) {
        this.date = new Date(date);
        this.description = description;
        this.id = id;
    }

    public void loadImageFromString(String image) {
        byte[] decodedString = Base64.decode(image, Base64.URL_SAFE);
        this.bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Date getDate() {
        return date;
    }

    private long secondsAgo(Date date) {
        return (new Date().getTime() - date.getTime());
    }

    private String formatSeconds(long millis) {
        long seconds = millis / 1000;

        if (seconds < 60) {
            return seconds + "s";
        }

        if (seconds < 3600)
            return (seconds / 60) + "m";

        if (seconds < 86400)
            return (seconds / 3600) + "h";

        if (seconds < 86400 * 31)
            return ((seconds / 3600) / 24) + "d";


        return ((seconds / 3600) / 24) / 31 + "M";
    }

    public String getDateString() {
        return formatSeconds(secondsAgo(date));
    }

    public boolean isLoading() {
        return loading;
    }

    public String getId() {
        return id;
    }

    public void setLoaded() {
        this.loaded = true;
        this.loading = false;
    }

    public void setFailed() {
        this.loaded = false;
        this.loading = false;
        this.failed = true;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setLoading() {
        this.loading = true;
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;

        if (other instanceof GalleryImage) {
            if (other.hashCode() == hashCode())
                equal = true;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
