package com.github.codingchili.model;

import java.util.Comparator;

/**
 * @author Robin Duda
 *
 * Compares two images, the images with the
 * higher date should have precedence, as it is
 * more recent.
 */

public class GalleryImageComparator implements Comparator<GalleryImage> {
    @Override
    public int compare(GalleryImage first, GalleryImage second) {
        long timeFirst = first.getDate().getTime();
        long timeSecond = second.getDate().getTime();

        if (timeFirst > timeSecond)
            return -1;
        else if (timeFirst != timeSecond)
            return 0;
        else
            return 1;
    }
}
