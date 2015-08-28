package com.chilimannen.onegram.view.Helper;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * Contains data for the report image option.
 */

public class ImageReport {
    private GalleryImage image;
    private ArrayList<String> reasons;

    public ImageReport(GalleryImage image, ArrayList<String> selected) {
        this.image = image;
        this.reasons = selected;
    }

    public String getImageId() {
        return image.getId();
    }

    public String getReasonString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < reasons.size(); i++) {
            builder.append(reasons.get(i)).append(",");
        }

        return builder.toString();
    }
}
