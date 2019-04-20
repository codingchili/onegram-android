package com.github.codingchili.api;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author Robin Duda
 *
 * Verifies any host name with a certificate, used with a self signed certificate.
 */


// todo remove when changing to a signed certificate.
public class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
}
