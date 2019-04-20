package com.codingchili.onegram.view.helper.filters;

/**
 * @author Robin Duda
 *
 *  Applies a grayfilter.
 */

public class GrayFilter implements FilterOperation {
    @Override
    public int get(int pixel) {

        int red = pixel & (0x00ff0000 >> 16);
        int green = pixel & (0x0000ff00 >> 8);
        int blue = pixel & 0x000000ff;
        int average = Math.round((red + green + blue) / 3);

        int gray = 0x000000ff;
        gray += average;
        gray = (gray << 8);

        gray += average;
        gray = (gray << 8);

        gray += average;
        gray = (gray << 8);

        return gray;
    }
}
