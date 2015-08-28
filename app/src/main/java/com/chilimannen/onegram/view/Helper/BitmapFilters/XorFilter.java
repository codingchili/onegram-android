package com.chilimannen.onegram.view.Helper.BitmapFilters;

/**
 * @author Robin Duda
 * Basic Xor filter.
 */

public class XorFilter implements FilterOperation {
    private int mask;

    public XorFilter(int mask) {
        this.mask = mask;
    }


    @Override
    public int get(int pixel) {
        return (pixel ^ mask);
    }
}
