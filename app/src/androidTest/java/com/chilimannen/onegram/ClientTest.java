package com.chilimannen.onegram;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.chilimannen.onegram.model.API.Client;
import com.chilimannen.onegram.model.API.ClientProtocol;
import com.chilimannen.onegram.model.API.Exception.QueryConflictException;
import com.chilimannen.onegram.model.API.Exception.QueryException;
import com.chilimannen.onegram.model.API.Response;
import com.chilimannen.onegram.view.Helper.CertificateHelper;
import com.chilimannen.onegram.view.Helper.GalleryImage;
import com.chilimannen.onegram.view.Helper.ImageReport;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Robin Duda
 *
 * Test cases for the client / protocol.
 */

public class ClientTest extends ApplicationTestCase<Application> {
    private static ClientProtocol client;
    private final String username = "eu.imagepool@gmail.com";
    private final String password = "gimli_the_dwarf";
    private String token = "";

    public ClientTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        client = new Client(token, CertificateHelper.getCertificate(getContext()));
    }

    public void testLogin() throws Exception {
        assertSuccess(client.login(username, password));
        token = client.getToken();
    }

    public void testRegister() throws Exception {
        try {
            assertSuccess(client.registerAndLogin(username, password));
            token = client.getToken();
        } catch (QueryConflictException ignored) {
            // already exists from previous test, just as good.
        }
    }

    public void testReportImage() throws Exception {
        assertSuccess(client.reportImage(
                new ImageReport(
                        new GalleryImage(
                                new Date().getTime(),
                                "",
                                ""), new ArrayList<String>())));
    }

    public void testUnfollowPost() throws Exception {
        assertSuccess(client.removeFromGallery("faux_id"));
    }

    public void testLoadgallery() throws Exception {
        assertSuccess(client.addToGallery("faux_id"));
    }

    private void assertSuccess(Response response) throws QueryException {
        if (response.hasException())
            throw response.getException();
    }
}
