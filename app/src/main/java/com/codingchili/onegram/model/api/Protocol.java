package com.codingchili.onegram.model.api;

import com.codingchili.onegram.view.helper.ImageReport;

/**
 * @author Robin Duda
 * <p>
 * Creates a Request object containing parameters.
 */

public class Protocol {
    private static String host;

    /**
     * @param hostname the hostname for the API.
     */
    public static void setHost(String hostname) {
        Protocol.host = hostname;
    }

    public static Request login(String username, String password) {
        String query = "username=%username%&password=%password%";
        query = query.replace("%username%", username);
        query = query.replace("%password%", password);
        return new Request(query, Method.Post, "/login", host);
    }

    public static Request register(String username, String password) {
        String query = "username=%username%&password=%password%";
        query = query.replace("%username%", username);
        query = query.replace("%password%", password);
        return new Request(query, Method.Post, "/register", host);
    }

    public static Request downloadGallery(Token token) {
        String query = "/api/browse/gallery?token=" + token.getKey();
        return new Request(query, Method.Get, query, host);
    }

    public static Request upload(String base64image, String description, Token token) {
        String query = "token=%token%&image=%image%&description=%description%";
        query = query.replace("%token%", token.getKey());
        query = query.replace("%image%", base64image);
        query = query.replace("%description%", description);
        return new Request(query, Method.Post, "/api/upload", host);
    }

    public static Request addToGallery(String id, Token token) {
        String query = "token=%token%&image=%image%";
        query = query.replace("%token%", token.getKey());
        query = query.replace("%image%", id);
        return new Request(query, Method.Post, "/api/save", host);
    }

    public static Request downloadImage(String id, Token token) {
        String query = "/api/browse/download?token=" + token.getKey() + "&image=" + id;
        return new Request(query, Method.Get, query, host);
    }

    public static Request downloadSearch(String tags, Token token) {
        tags = tags.replace(" ", ",");
        String query = "/api/browse/tags?token=" + token.getKey() + "&tags=" + tags;
        return new Request(query, Method.Get, query, host);
    }

    public static Request removeFromGallery(String id, Token token) {
        String query = "token=%token%&image=%image%";
        query = query.replace("%token%", token.getKey());
        query = query.replace("%image%", id);
        return new Request(query, Method.Post, "/api/unsave", host);
    }

    public static Request getTagSuggestions(String search, Token token) {
        String query = "/api/browse/tagcompletion?token=" + token.getKey() + "&search=" + search;
        return new Request(query, Method.Get, query, host);
    }

    public static Request reportImage(ImageReport report, Token token) {
        String query = "token=%token%&image=%image%&reason=%reason%";
        query = query.replace("%token%", token.getKey());
        query = query.replace("%image%", report.getImageId());
        query = query.replace("%reason%", report.getReasonString());
        return new Request(query, Method.Post, "/api/report", host);
    }
}
