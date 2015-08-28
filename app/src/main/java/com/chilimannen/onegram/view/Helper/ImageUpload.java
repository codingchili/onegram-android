package com.chilimannen.onegram.view.Helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;

import com.chilimannen.onegram.view.Helper.BitmapFilters.BitmapFilters;

import java.io.ByteArrayOutputStream;

/**
 * @author Robin Duda
 *
 * Contains image data for an image being uploaded or
 * is in the upload queue.
 */

public class ImageUpload {
    private Boolean saveToGallery;
    private Boolean isUploading;
    private Bitmap source;
    private Bitmap bitmap;
    private String description = "";

    public ImageUpload(Bitmap source) {
        this.source = source;
        this.bitmap = source;
        this.isUploading = false;
    }


    public String getDescription() {
        return this.description;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setFilter(BitmapFilters.Filter filter) {
        switch (filter) {
            case Scream:
                bitmap = BitmapFilters.scream(source);
                break;
            case Darken:
                bitmap = BitmapFilters.darken(source);
            break;
            case Painting:
                bitmap = BitmapFilters.painting(source);
            break;
            case Burn:
                bitmap = BitmapFilters.burn(source);
            break;
            case Red:
                bitmap = BitmapFilters.red(source);
            break;
            case Green:
                bitmap = BitmapFilters.green(source);
            break;
            case Blue:
                bitmap = BitmapFilters.blue(source);
            break;
            default:
                bitmap = source;
                break;
        }
    }

    public Boolean isUploading() {
        return this.isUploading;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setUploading(boolean uploading) {
        this.isUploading = uploading;
    }

    public String getBitmapBase64() {
        Bitmap bitmap = getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.URL_SAFE);
    }

    public void addRotation(int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public boolean isSaveToGallery() {
        return saveToGallery;
    }

    public void setSaveToGallery(boolean saveToGallery) {
        this.saveToGallery = saveToGallery;
    }
}
