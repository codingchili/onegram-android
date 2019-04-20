package com.github.codingchili.model.filters;

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
