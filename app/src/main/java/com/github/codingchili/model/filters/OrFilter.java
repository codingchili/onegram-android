package com.github.codingchili.model.filters;

/**
 * @author Robin Duda
 *
 * Basic Or filter.
 */

public class OrFilter implements FilterOperation {
    private int mask;

    public OrFilter(int mask) {
        this.mask = mask;
    }

    @Override
    public int get(int pixel) {
        return (pixel | mask);
    }
}
