package com.codingchili.onegram.model.api;

/**
 * @author Robin Duda
 *
 * Stores the authentication token for the Client.
 */

public class Token {
    private String key;

    public Token(String key) {
        this.key = key;
    }

    public Token() {

    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return (key != null) ? key : "";
    }

}
