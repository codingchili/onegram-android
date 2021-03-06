package com.github.codingchili.api;

import com.github.codingchili.api.exception.AccountNotVerifiedException;
import com.github.codingchili.api.exception.EmailInvalidException;
import com.github.codingchili.api.exception.QueryConflictException;
import com.github.codingchili.api.exception.QueryErrorException;
import com.github.codingchili.api.exception.QueryException;
import com.github.codingchili.api.exception.QueryMissingException;
import com.github.codingchili.api.exception.QueryNotAcceptedException;
import com.github.codingchili.api.exception.QueryTooLargeException;
import com.github.codingchili.api.exception.QueryUnauthenticatedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author Robin Duda
 *
 * Response object created from the Query to the server.
 */

public class Response {
    private String data = "";
    private long ping = 0;
    private QueryException exception = null;

    public Response() {
    }

    public Response(String data, Integer status) {
        this.data = data;
        this.exception = setException(status);
    }


    public QueryException getException() {
        return exception;
    }

    public String getData() {
        return data;
    }

    public JSONObject getJSON() throws JSONException {
        return new JSONObject(data);
    }

    /**
     * @param httpStatus HTTP status code.
     */
    public void setStatus(Integer httpStatus) {
        this.exception = setException(httpStatus);
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean hasException() {
        return exception != null;
    }

    /**
     * Creates an exception from an HTTP status code.
     */
    private QueryException setException(Integer httpStatus) {
        switch (httpStatus) {
            case 400:
                return new QueryErrorException();
            case 401:
                return new QueryUnauthenticatedException();
            case 403:
                return new AccountNotVerifiedException();
            case 404:
                return new QueryMissingException();
            case 405:
                return new EmailInvalidException();
            case 406:
                return new QueryNotAcceptedException();
            case 409:
                return new QueryConflictException();
            case 413:
                return new QueryTooLargeException();
            default:
                return null;
        }
    }

    public void setException(QueryException exception) {
        this.exception = exception;
    }

    /**
     * @return response size in bytes.
     */
    public int getSize() {
        return data.length();
    }

    /**
     * @return query ping time in ms.
     */
    public long getPing() {
        return ping;
    }

    /**
     * @param ping in ms.
     */
    public void setPing(long ping) {
        this.ping = ping;
    }

    public boolean hasData() {
        return (!data.equals(""));
    }

    @Override
    public String toString() {
        return ((data == null) ? "" : data)
                + ", "
                + ((exception == null) ? "" : Arrays.toString(exception.getStackTrace()));
    }
}
