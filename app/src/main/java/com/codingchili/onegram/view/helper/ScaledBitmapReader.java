package com.codingchili.onegram.view.helper;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Robin Duda
 *
 * Rescales a bitmap for mobile devices.
 */

public class ScaledBitmapReader {
    private static final int size = 255;


    /**
     * Android Documentation
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap readFile(File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        options.inSampleSize = calculateInSampleSize(options, size, size);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static Bitmap readStream(Uri resource, ContentResolver resolver) throws FileNotFoundException {
        InputStream stream = resolver.openInputStream(resource);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        stream = resolver.openInputStream(resource);

        options.inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(stream, null, options);
    }
}
