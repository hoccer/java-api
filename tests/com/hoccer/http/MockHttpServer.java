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

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class MockHttpServer extends NanoHTTPD {

    private static String                 LOG_TAG        = "MockHttpServer";
    private static int                    PORT           = 4000;
    private final HashMap<String, String> mSubResources  = new HashMap<String, String>();
    private int                           mResponseDelay = 0;
    private ClientRequest                 mLastRequest;
    private ClientRequest                 mLastPost;
    private Response                      mLastResponse;

    public MockHttpServer() throws IOException {
        super(PORT);
    }

    public void setResponseDelay(int pMilliseconds) {
        mResponseDelay = pMilliseconds;
    }

    ClientRequest getLastRequest() {
        return mLastRequest;
    }

    ClientRequest getLastPost() {
        return mLastPost;
    }

    Response getLastResponse() {
        return mLastResponse;
    }

    @Override
    public Response serve(ClientRequest request) {
        mLastRequest = request;
        mLastResponse = handleRequest(request);
        return mLastResponse;
    }

    private Response handleRequest(ClientRequest request) {
        try {
            Thread.sleep(mResponseDelay);
        } catch (InterruptedException e) {
        }

        String acceptedMimeType = request.header.getProperty("accept", NanoHTTPD.MIME_PLAINTEXT);
        if (!acceptedMimeType.equals(NanoHTTPD.MIME_PLAINTEXT)) {
            return new NanoHTTPD.Response(HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "accept mime-type '"
                    + acceptedMimeType + "' is not implemented ");
        }

        String contentType = request.header.getProperty("content-type", NanoHTTPD.MIME_PLAINTEXT);
        if (!contentType.equals(NanoHTTPD.MIME_PLAINTEXT)
                && !contentType.equals(NanoHTTPD.MIME_XML)
                && !contentType.equals(NanoHTTPD.MIME_FORM_URLENCODED)
                && !contentType.startsWith(NanoHTTPD.MIME_MULTIPART)) {
            return new NanoHTTPD.Response(HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "content-type '"
                    + contentType + "' is not implemented ");
        }

        if (request.method.equals("PUT")) {
            return handlePutRequest(request);
        }

        if (!request.uri.equals("/") && !mSubResources.containsKey(request.uri)) {
            return new NanoHTTPD.Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Not Found");
        }

        if (request.method.equals("GET")) {
            return handleGetRequest(request);
        }

        if (request.method.equals("POST")) {
            return handlePostRequest(request);
        }

        return new NanoHTTPD.Response(HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "not implemented");
    }

    private NanoHTTPD.Response handlePostRequest(ClientRequest request) {

        mLastPost = request;

        String newResource = "/" + UUID.randomUUID();
        String contentType = request.header.getProperty("content-type", "text/plain");
        String data = "this text should not but was produced by content-type '" + contentType + "'";
        if (contentType.equals(NanoHTTPD.MIME_FORM_URLENCODED)) {
            data = request.parameters.getProperty("message",
                    "no message property given (use http//...?message=<your message>");
        } else if (contentType.equals(NanoHTTPD.MIME_PLAINTEXT)) {
            data = request.body.length() == 0 ? "no data posted" : request.body;
        } else if (contentType.equals("")) {
            return new NanoHTTPD.Response(HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "content-type "
                    + contentType + " is not implemented");
        }

        mSubResources.put(newResource, data);

        NanoHTTPD.Response response;
        response = new NanoHTTPD.Response(HTTP_REDIRECT, MIME_PLAINTEXT, "see other: "
                + "http://localhost:" + PORT + newResource);
        response.addHeader("Location", "http://localhost:" + PORT + newResource);
        return response;
    }

    private Response handleGetRequest(ClientRequest request) {
        String msg = null;
        if (mSubResources.containsKey(request.uri)) {
            msg = mSubResources.get(request.uri);
        } else {
            msg = "I'm a mock server for test purposes";
        }
        return new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, msg);
    }

    private Response handlePutRequest(ClientRequest request) {
        if (request.uri.equals("/")) {
            return new NanoHTTPD.Response(HTTP_FORBIDDEN, MIME_PLAINTEXT,
                    "to create a resource at '/' you need to use HTTP POST");
        }

        mSubResources.put(request.uri, request.body);
        NanoHTTPD.Response response = new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, request.body);
        response.addHeader("Content-length", "" + request.body.getBytes().length);
        return response;
    }

    /**
     * @return basic location and port of the server
     */
    public String getUri() {
        return "http://localhost:" + PORT;
    }
}
