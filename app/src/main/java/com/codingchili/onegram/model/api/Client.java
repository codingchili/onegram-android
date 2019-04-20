package com.codingchili.onegram.model.api;

import com.codingchili.onegram.view.helper.ImageReport;
import com.codingchili.onegram.view.helper.ImageUpload;
import com.codingchili.onegram.view.helper.NetworkStatisticsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.Certificate;

/**
 * @author Robin Duda
 *
 * Communicating with the server and handling certificates
 * and tokens in the process. This client also implements
 * network statistics in the local SQLite database.
 *
 * The client uses a web service to communicate with the server.
 */

public class Client implements ClientProtocol {
    private Token token = new Token();
    private Certificate certificate;
    private NetworkStatisticsHandler statHandler;

    public Client(Certificate certificate) {
        this.certificate = certificate;
    }

    public Client(String token, Certificate certificate) {
        this.token = new Token(token);
        this.certificate = certificate;
    }

    @Override
    public void setStatisticsHandler(NetworkStatisticsHandler statHandler) {
        this.statHandler = statHandler;
    }

    private void getToken(Response response) {
        try {
            JSONObject data = new JSONObject(response.getData());
            token.setKey(data.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // has to be synchronized as the NetworkStatisticsHandler is not thread safe.
    private synchronized Query addStats(Query query) {
        if (statHandler != null)
            statHandler.addRequest(query);

        return query;
    }

    @Override
    public Response registerAndLogin(String username, String password) {
        Response response = addStats(new Query(
                Protocol.register(username, password),
                certificate)).getResponse();

        // if the registration failed, do not attempt to login.
        if (response.hasException())
            return response;

        return login(username, password);
    }

    @Override
    public Response login(String username, String password) {
        Response response = addStats(new Query(
                Protocol.login(username, password),
                certificate)).getResponse();

        getToken(response);
        return response;
    }

    @Override
    public Response getGallery() {
        return addStats(new Query(
                Protocol.downloadGallery(token),
                certificate))
                .getResponse();
    }

    @Override
    public Response uploadImage(ImageUpload image) {
        return addStats(new Query(
                Protocol.upload(image.getBitmapBase64(), image.getDescription(), token),
                certificate))
                .getResponse();
    }

    @Override
    public Response addToGallery(String id) {
        return addStats(new Query(
                Protocol.addToGallery(id, token),
                certificate))
                .getResponse();
    }

    @Override
    public Response downloadImage(String id) {
        return addStats(new Query(
                Protocol.downloadImage(id, token),
                certificate))
                .getResponse();
    }

    @Override
    public Response getSearch(String tags) {
        return addStats(new Query(
                Protocol.downloadSearch(tags, token),
                certificate))
                .getResponse();
    }

    @Override
    public Response removeFromGallery(String id) {
        return addStats(new Query(
                Protocol.removeFromGallery(id, token),
                certificate))
                .getResponse();
    }

    @Override
    public Response getTagSuggestions(String search) {
        return addStats(new Query(
                Protocol.getTagSuggestions(search, token),
                certificate))
                .getResponse();
    }

    @Override
    public Response reportImage(ImageReport report) {
        return addStats(new Query(
                Protocol.reportImage(report, token),
                certificate))
                .getResponse();
    }

    @Override
    public String getToken() {
        return token.getKey();
    }

}
