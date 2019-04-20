package com.github.codingchili.api;

import com.github.codingchili.model.ImageReport;
import com.github.codingchili.model.ImageUpload;
import com.github.codingchili.model.NetworkStatisticsHandler;

/**
 * @author Robin Duda
 *
 * Interface defining the operations that has to be supported by the
 * implementor client in order to achieve full functionality.
 *
 * All methods returns a Response object which contains the
 * query response string and a QueryException object indicating
 * if any and of which type of errors occured during the query.
 *
 * The errors thrown are only related to the protocol.
 */

public interface ClientProtocol {
    /**
     * Attempts to register a new account and then login.
     */
    Response registerAndLogin(String username, String password);

    /**
     * This method should store the login token if the login
     * completed successfully and reuse it for all queries.
     */
    Response login(String username, String password);

    /**
     * Returns the gallery data of the user owning
     * the passed token if the token was valid.
     * The gallery should contain image metadata only.
     */
    Response getGallery();

    /**
     * Uploads an image in the context of the user that
     * owns the passed session token.
     */
    Response uploadImage(ImageUpload image);

    /**
     * @param id of the image that the server should add
     *           to the user owning the passed session token.
     * @return the image id so that it may be added to the gallery.
     */
    Response addToGallery(String id);

    /**
     * @param id of the image that should be removed from
     *           the gallery of the token owner.
     */
    Response removeFromGallery(String id);

    /**
     * @param id of the image that should be downloaded
     *           only the image data of the requested image
     *           should be fetched.
     */
    Response downloadImage(String id);

    /**
     * @param tags contains the strings that should be searched
     *             for on the server. The tags should be separated
     *             by a single space.
     * @return the image data.
     */
    Response getSearch(String tags);

    /**
     * @param search is an incomplete tag search that should be
     *               given suggestions for on how to complete.
     * @return should contain data with an array of strings.
     */
    Response getTagSuggestions(String search);

    /**
     * @param imageReport contains an image id and an array
     *                    of report reasons.
     */
    Response reportImage(ImageReport imageReport);

    /**
     * @param statHandler statistics handler for in/out and ping.
     */
    void setStatisticsHandler(NetworkStatisticsHandler statHandler);

    /**
     * Returns the token string for the connection authenticating the user.
     */
    String getToken();
}
