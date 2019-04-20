package com.codingchili.onegram.view.helper;

import com.codingchili.onegram.model.api.Query;

/**
 * @author Robin Duda
 *
 * Network statistics interface.
 */

public interface NetworkStatisticsHandler {
    /**
     * @param query added to the statistics,
     *              contains the request and
     *              response length and the
     *              response ping time.
     */
    public void addRequest(Query query);

    /**
     * @return the average ping formatted in ms.
     */
    public String getPingAverage();

    /**
     * @return the total transmitted bytes formatted.
     */
    public String getUploaded();

    /**
     * @return the total amount of received bytes formatted.
     */
    public String getDownloaded();
}
