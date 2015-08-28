package com.chilimannen.onegram.view.Helper.BitmapFilters;

/**
 * @author Robin Duda
 */

/**
 * Specifies an operation that can be done on a single pixel.
 */
public interface FilterOperation {
    public int get(int pixel);
}
