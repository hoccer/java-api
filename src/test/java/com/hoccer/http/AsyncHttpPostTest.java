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
package com.hoccer.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.hoccer.tools.TestHelper;

public class AsyncHttpPostTest extends AsyncHttpTestCase {

    AsyncHttpPost               mRequest;

    public void testExecution() throws Exception {

        getServer().setResponseDelay(200);
        mRequest = new AsyncHttpPost(getServer().getUri());
        mRequest.start();

        TestHelper.blockUntilTrue("request should have started by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 0;
                    }
                });
        assertTrue("request should have started, but progress is " + mRequest.getProgress() + "%",
                mRequest.isRunning());

        TestHelper.blockUntilTrue("request should have got the response by now", 4000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 1;
                    }
                });

        blockUntilRequestIsDone(mRequest);
    }

    public void testEmptyPost() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri());
        mRequest.start();

        TestHelper.blockUntilEquals("request should have been performed by now", 2000,
                "no data posted", new TestHelper.Measurement() {

                    @Override
                    public Object getActualValue() throws Exception {
                        return mRequest.getBodyAsString();
                    }
                });
    }

    public void testPostWithStringBody() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri());
        mRequest.setBody("test post");
        mRequest.start();
        blockUntilRequestIsDone(mRequest);
        assertEquals("should receive what was posted", "test post", mRequest.getBodyAsString());
    }

    public void testPostWithLongStringBody() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri());
        String content = "test post which is \nreally really long\n\n.... this is the\nlast line ";
        mRequest.setBody(content);
        mRequest.start();
        blockUntilRequestIsDone(mRequest);
        assertEquals("mock server should receive posted data", content,
                getServer().getLastPost().body);
        assertEquals("should be redirected to what was posted", content, mRequest.getBodyAsString());
    }

    public void testPostWithUrlEncodedParametersAsHashMap() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri());
        Map<String, String> params = new HashMap<String, String>();
        params.put("message", "test post with hash map");
        mRequest.setBody(params);
        mRequest.start();
        blockUntilRequestIsDone(mRequest);
        assertEquals("should receive what was posted", "test post with hash map", mRequest
                .getBodyAsString());
    }

    public void testGettingBodyWithoutPerformingTheRequest() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri());
        TestHelper.blockUntilEquals("request should provide empty body before started", 1000, "",
                new TestHelper.Measurement() {

                    @Override
                    public Object getActualValue() throws Exception {
                        return mRequest.getBodyAsString();
                    }
                });
    }

    public void testGettingNoifiedAbooutSuccessViaResponseHandler() throws Exception {

        getServer().setResponseDelay(200);

        mRequest = new AsyncHttpPost(getServer().getUri());
        ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mRequest.registerResponseHandler(requestStatus);
        mRequest.start();
        TestHelper.blockUntilTrue("request should have started", 1000, new TestHelper.Condition() {

            @Override
            public boolean isSatisfied() throws Exception {
                return mRequest.isRunning();
            }
        });

        blockUntilHeadersAvailable(requestStatus);
        blockUntilRequestIsDone(mRequest);
        assertTrue("should be successful", requestStatus.wasSuccessful);
        assertNotNull("should have an response body", requestStatus.body);
        assertEquals("response should come from mocked server", "no data posted",
                requestStatus.body.toString());
    }

    public void testGettingNoifiedAbooutFailureViaResponseHandler() throws Exception {

        mRequest = new AsyncHttpPost(getServer().getUri() + "/not-a-valid-address");
        final ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mRequest.registerResponseHandler(requestStatus);
        mRequest.start();
        TestHelper.blockUntilTrue("request should have called onError", 2000,
                new TestHelper.Condition() {

                    @Override
                    public boolean isSatisfied() throws Exception {
                        return requestStatus.hasError;
                    }
                });
        assertFalse("should not be successful", requestStatus.wasSuccessful);
        assertNotNull("should have no response body", requestStatus.body);
        assertEquals("response from mocked server should describe 404", "Not Found",
                requestStatus.body.toString());
    }

    public void testCustomHttpClientToSupressRedirectionAfterPost() throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);
        DefaultHttpClient httpClient = new HttpClientWithKeystore(httpParams);
        mRequest = new AsyncHttpPost(getServer().getUri(), httpClient);
        mRequest.start();
        blockUntilRequestIsDone(mRequest);
        assertTrue("should have an location header", mRequest.getHeader("Location").contains(
                "http://localhost"));
        TestHelper.assertIncludes("response from mocked server should describe 303",
                "see other: http://localhost", mRequest.getBodyAsString().toString());
    }

    public void testUriOfSolvableRequest() throws Exception {
        mRequest = new AsyncHttpPost(getServer().getUri());
        assertEquals("should get uri of creation", getServer().getUri(), mRequest.getUri());
        mRequest.start();
        assertEquals("should get uri of creation", getServer().getUri(), mRequest.getUri());
        blockUntilRequestIsDone(mRequest);
        assertEquals(
                "uri should now point to resource whre the post request got redirected indirectly",
                getServer().getUri() + getServer().getLastRequest().uri, mRequest.getUri());
    }

    public void testUriOf404Request() throws Exception {
        String uri = getServer().getUri() + "/not-existing";
        mRequest = new AsyncHttpPost(uri);
        assertEquals("should get uri of creation", uri, mRequest.getUri());
        mRequest.start();
        assertEquals("should get uri of creation", uri, mRequest.getUri());
        blockUntilRequestIsDone(mRequest);
        assertEquals("should get uri of creation", uri, mRequest.getUri());
    }
}
