/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestRESTfulApi {

    DefaultHttpClient mHttpClient;

    public TestRESTfulApi() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        mHttpClient = new DefaultHttpClient(cm, httpParams);
    }

    @Test
    public void testPostingNonexistentClientId() throws Exception {

        HttpPost request = new HttpPost(ClientConfig.getLinccerBaseUri() + "clients/"
                + UUID.randomUUID().toString() + "/environment");
        request.setEntity(new StringEntity("{}"));
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get error for bad operation on client uri", 404, response
                .getStatusLine().getStatusCode());
    }

    @Test
    public void testSigningOffNonexistingClient() throws Exception {

        String uri = ClientConfig.getLinccerBaseUri() + "/clients/" + UUID.randomUUID().toString()
                + "/environment";

        HttpDelete request = new HttpDelete(uri);
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should be able to sign off noexisting client uri", 200, response
                .getStatusLine().getStatusCode());
    }

    @Test(timeout = 4000)
    public void testSigningOffJustCreatedClient() throws Exception {

        String client = ClientConfig.getLinccerBaseUri() + "/clients/"
                + UUID.randomUUID().toString();

        publishPosition(client + "/environment");

        HttpResponse response = receiveOneToOne(client);
        // response.getEntity().consumeContent();

        HttpDelete request = new HttpDelete(client + "/environment");
        response = mHttpClient.execute(request);
        response.getEntity().consumeContent();
        assertEquals("should be able to sign off client", 200, response.getStatusLine()
                .getStatusCode());
    }

    @Test(timeout = 1000)
    public void lonlyReceive() throws Exception {

        String client = ClientConfig.getLinccerBaseUri()
                + "/clients/c278d820-d1f0-11df-bd3b-0800200c9a66";

        publishPosition(client + "/environment");

        HttpResponse response = receiveOneToOne(client);
        String body = (response.getEntity() != null) ? EntityUtils.toString(response.getEntity())
                : "<no body>";
        assertEquals("should have been told about 'no data' but server said " + body, 204, response
                .getStatusLine().getStatusCode());
    }

    private HttpResponse receiveOneToOne(String client) throws IOException, ClientProtocolException {
        HttpGet receive = new HttpGet(client + "/action/one-to-one");
        HttpResponse response;
        response = mHttpClient.execute(receive);
        mHttpClient.getConnectionManager().closeIdleConnections(1, TimeUnit.MICROSECONDS);

        return response;
    }

    private int latitude = 13;

    private void publishPosition(String uri) throws UnsupportedEncodingException, IOException,
            ClientProtocolException {
        HttpPut envUpdate = new HttpPut(ApiSigningTools.sign(uri, TestApiKeySigning.demoKey,
                TestApiKeySigning.demoSecret));
        envUpdate.setEntity(new StringEntity("{\"gps\": {\"longitude\": " + latitude++
                + ", \"latitude\": 50, \"accuracy\": 100} }"));
        HttpResponse response = mHttpClient.execute(envUpdate);
        assertEquals("should have updated the environment but server said "
                + EntityUtils.toString(response.getEntity()), 201, response.getStatusLine()
                .getStatusCode());
        envUpdate.abort();
    }
}
