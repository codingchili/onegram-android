package com.chilimannen.onegram.view.Helper.BitmapFilters;

import android.graphics.Bitmap;

/**
 * @author Robin Duda
 *
 * Applies filter operations to a bitmap source.
 */

public class BitmapFilters {

    public static Bitmap scream(Bitmap source) {
        return baseFilter(source, new XorFilter(0xac9c));
    }

    public static Bitmap burn(Bitmap source) {
        return baseFilter(source, new AddFilter(0xff1f1f1f));
    }

    public static Bitmap painting(Bitmap source) {
        return baseFilter(source, new AbstractFilter());
    }

    public static Bitmap darken(Bitmap source) {
        return baseFilter(source, new GrayFilter());
    }

    public static Bitmap red(Bitmap source) {
        return baseFilter(source, new AndFilter(0xff00ffff));
    }

    public static Bitmap green(Bitmap source) {
        return baseFilter(source, new AndFilter(0xffff00ff));
    }

    public static Bitmap blue(Bitmap source) {
        return baseFilter(source, new AndFilter(0xffffff00));
    }

    private static Bitmap baseFilter(Bitmap source, FilterOperation operation) {
        Bitmap bitmap = source.copy(Bitmap.Config.ARGB_8888, true);

        for (int y = 0; y < bitmap.getHeight(); y++) {
        for (int x = 0; x < bitmap.getWidth(); x++) {
                int p = bitmap.getPixel(x, y);
                bitmap.setPixel(x, y, operation.get(p));
            }
        }

        return bitmap;
    }

    public static enum Filter {Scream, None, Darken, Burn, Red, Green, Blue, Painting}
}
