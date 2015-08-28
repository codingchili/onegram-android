package com.chilimannen.onegram.view.Helper.BitmapFilters;

/**
 * @author Robin Duda
 *
 * Basic Not filter.
 */

public class NotFilter implements FilterOperation {
    public int get(int pixel) {
        return ~pixel;
    }
 }
