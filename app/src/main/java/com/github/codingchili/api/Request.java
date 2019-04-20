package com.github.codingchili.api;

/**
 * @author Robin Duda
 *
 * Request object required by the ClientProtocol interface.
 */

public class Request {
    private String query;
    private Method method;
    private String resource;
    private String host;

    public String getHost() {
        return host;
    }

    public String getResource() {
        return resource;
    }

    public Method getMethod() {
        return method;
    }

    public String getQuery() {
        return query;
    }


    public Request(String query, Method method, String resource, String host) {
        this.query = query;
        this.method = method;
        this.host = host;
        this.resource = resource;
    }

    public int getSize() {
        return query.length();
    }
}
