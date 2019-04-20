package com.codingchili.onegram.view.helper.filters;

/**
 * @author Robin Duda
 *
 * Performs some wicked conversion of pixels.
 */

public class AbstractFilter implements FilterOperation {
    @Override
    public int get(int pixel) {
        int red = (pixel & 0xffff0000) - 0x0000ffff;
        int green = (pixel & 0xff00ff00) - 0x0000ff;
        int blue = pixel & 0xff0000ff;
        int average = Math.round((red + green + blue) / 3);

        int gray = 0x000000ff;
        gray = (gray << 8);
        gray += average;

        gray = (gray << 8);
        gray += average;

        gray = (gray << 8);
        gray += average;

        return gray;
    }
}
