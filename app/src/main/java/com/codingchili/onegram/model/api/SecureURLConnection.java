package com.codingchili.onegram.model.api;

import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Robin Duda
 *
 * Loads a certificate from a predefined file.
 */

public class SecureURLConnection {


    /**
     * Original Author: Andrew Smith
     * URL: http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/
     * Enables the use of self signed certificates.
     */
    public static HttpsURLConnection setUp(URL url, Certificate certificate) throws Exception {
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);

            return urlConnection;
    }
}