package com.github.codingchili.model.filters;

/**
 * @author Robin Duda
 *
 * Basic add filter.
 */

public class AddFilter implements FilterOperation {
    private int mask;

    public AddFilter(int mask) {
        this.mask = mask;
    }

    @Override
    public int get(int pixel) {
        return (pixel + mask);
    }
}
