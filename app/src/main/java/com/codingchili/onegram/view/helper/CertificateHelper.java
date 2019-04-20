package com.codingchili.onegram.view.helper;

import android.content.Context;

import com.codingchili.onegram.R;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * @author Robin Duda
 *
 * Loads a certificate from the raw assets.
 */

public class CertificateHelper {
    public static Certificate getCertificate(Context context) {
        Certificate certificate = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream stream = context.getResources().openRawResource(R.raw.server);
            certificate = cf.generateCertificate(stream);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return certificate;
    }
}
