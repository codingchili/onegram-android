package com.codingchili.onegram.view.helper.filters;

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
