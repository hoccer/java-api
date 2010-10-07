package com.hoccer.api;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class TestRESTfulApi {

    DefaultHttpClient mHttpClient = new DefaultHttpClient();

    @Test
    public void testReadingNonexistentClientId() throws Exception {

        HttpPost request = new HttpPost(ClientDescription.mRemoteServer
                + "/c278d820-d1f0-11df-bd3b-0800200c9a66");
        request.setEntity(new StringEntity("{}"));
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get 'precondition failed' error for nonexisten client uri", 404,
                response.getStatusLine().getStatusCode());
    }
}
