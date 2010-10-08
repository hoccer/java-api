package com.hoccer.api;

import static org.junit.Assert.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.junit.*;

public class TestRESTfulApi {

    DefaultHttpClient mHttpClient = new DefaultHttpClient();

    @Test
    public void testReadingNonexistentClientId() throws Exception {

        HttpPost request = new HttpPost(ClientDescription.getRemoteServer()
                + "/c278d820-d1f0-11df-bd3b-0800200c9a66");
        request.setEntity(new StringEntity("{}"));
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get 'precondition failed' error for nonexisten client uri", 404,
                response.getStatusLine().getStatusCode());
    }
}
