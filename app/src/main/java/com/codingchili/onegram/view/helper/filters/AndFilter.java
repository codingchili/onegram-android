package com.codingchili.onegram.view.helper.filters;

/**
 * @author Robin Duda
 *
 * Basic And filter.
 */
public class AndFilter implements FilterOperation {
    private int mask;

    public AndFilter(int mask) {
        this.mask = mask;
    }

    @Override
    public int get(int pixel) {
        return (mask & pixel);
    }
}
