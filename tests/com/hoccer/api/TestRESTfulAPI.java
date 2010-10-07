package com.hoccer.api;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class TestRESTfulAPI extends TestCase {

    DefaultHttpClient mHttpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mHttpClient = new DefaultHttpClient();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReadingNonexistentClientId() throws Exception {

        HttpPost request = new HttpPost(ClientConfig.mRemoteServer
                + "/c278d820-d1f0-11df-bd3b-0800200c9a66");
        request.setEntity(new StringEntity("{}"));
        HttpResponse response = mHttpClient.execute(request);
        assertEquals("should get 'precondition failed' error for nonexisten client uri", 412,
                response.getStatusLine().getStatusCode());
    }
}
