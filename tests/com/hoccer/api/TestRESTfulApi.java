/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Linccer GmbH Berlin, and are copy protected
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestRESTfulApi {

    DefaultHttpClient mHttpClient = new DefaultHttpClient();

    @Test
    public void testPostingNonexistentClientId() throws Exception {

        HttpPost request = new HttpPost(ClientConfig.getLinccerBaseUri()
                + "/c278d820-d1f0-11df-bd3b-0800200c9a66");
        request.setEntity(new StringEntity("{}"));
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get error for bad operation on client uri", 404,
                response.getStatusLine().getStatusCode());
    }

    @Test
    public void testSigningOffNonexistingClient() throws Exception {

        HttpDelete request = new HttpDelete(ClientConfig.getLinccerBaseUri()
                + "/c278d820-d1f0-11df-bd3b-0800200c9a66/environment");
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get error for nonexisten client uri", 404,
                response.getStatusLine().getStatusCode());
    }

    @Test(timeout = 1000)
    public void lonlyReceive() throws Exception {

        String uri = ClientConfig.getLinccerBaseUri()
                + "/clients/c278d820-d1f0-11df-bd3b-0800200c9a66/environment";

        HttpPut envUpdate = new HttpPut(ApiSigningTools.sign(uri, TestApiKeySigning.demoKey,
                TestApiKeySigning.demoSecret));
        envUpdate.setEntity(new StringEntity(
                "{\"gps\": {\"longitude\": 13, \"latitude\": 50, \"accuracy\": 100} }"));
        HttpResponse response = mHttpClient.execute(envUpdate);
        assertEquals("should have updated the environment but server said "
                + EntityUtils.toString(response.getEntity()), 201, response.getStatusLine()
                .getStatusCode());
        envUpdate.abort();

        HttpGet receive = new HttpGet(ClientConfig.getLinccerBaseUri()
                + "/clients/c278d820-d1f0-11df-bd3b-0800200c9a66/action/one-to-one");
        response = mHttpClient.execute(receive);
        String body = (response.getEntity() != null) ? EntityUtils.toString(response.getEntity())
                : "<no body>";
        assertEquals("should have been told about 'no data' but server said " + body, 204, response
                .getStatusLine().getStatusCode());
    }
}
