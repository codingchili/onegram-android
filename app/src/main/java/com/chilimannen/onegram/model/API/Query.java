package com.chilimannen.onegram.model.API;

import com.chilimannen.onegram.model.API.Exception.QueryConnectionException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Robin Duda
 *
 * Sends a query message to the server and
 * stores the response for retrieval.
 */

//todo implement put/patch/delete
public class Query {
    private Response response = new Response();
    private Request request;
    private Certificate certificate;

    public Query(Request request, Certificate certificate) {
        this.certificate = certificate;
        this.request = request;

        try {
            switch (request.getMethod()) {
                case Get:
                    QueryGet(request);
                    break;
                case Post:
                    QueryPost(request);
                    break;
            }
        } catch (Exception e) {
            // any exceptions at this point are considered connection exceptions.
            e.printStackTrace();
            response.setException(new QueryConnectionException());
        }
    }

    public Request getRequest() {
        return request;
    }

    private void QueryGet(Request request) throws Exception {
        URL url = new URL(request.getHost() + request.getResource());
        HttpsURLConnection urlConnection = SecureURLConnection.setUp(url, certificate);

        try {
            long time = System.currentTimeMillis();
            response.setStatus(urlConnection.getResponseCode());
            response.setPing(System.currentTimeMillis() - time);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response.setData(readStream(in));

        } catch (ConnectException e) {
            response.setException(new QueryConnectionException());
        } catch (IOException e) {
            // some status codes may only be retrieved after the exception has been thrown.
            response.setStatus(urlConnection.getResponseCode());
        }
    }

    private void QueryPost(Request request) throws Exception {
        URL url = null;
        url = new URL(request.getHost() + request.getResource());

        HttpsURLConnection urlConnection = SecureURLConnection.setUp(url, certificate);
        try {
            long time = System.currentTimeMillis();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();
            response.setPing(System.currentTimeMillis() - time);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out, request.getQuery());
            response.setStatus(urlConnection.getResponseCode());

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response.setData(readStream(in));

        } catch (ConnectException e) {
            response.setException(new QueryConnectionException());
        } catch (IOException e) {
            // some status codes may only be retrieved after the exception has been thrown.
            response.setStatus(urlConnection.getResponseCode());
        }

    }

    public Response getResponse() {
        return response;
    }

    /**
     * @param out an output stream pointing towards a socket.
     * @param query is the data to be written to the stream.
     */
    private void writeStream(OutputStream out, String query) {
        try {
            out.write(query.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param in is a stream reading from the socket.
     * @return the data that was read from the socket.
     */
    private String readStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

}
