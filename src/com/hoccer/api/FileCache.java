/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com> These coded instructions,
 * statements, and computer programs contain proprietary information of Hoccer GmbH Berlin, and are
 * copy protected by law. They may be used, modified and redistributed under the terms of GNU
 * General Public License referenced below. Alternative licensing without the obligations of the GPL
 * is available upon request. GPL v3 Licensing: This file is part of the "Linccer Java-API". Linccer
 * Java-API is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Linccer Java-API is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with Linccer Java-API. If
 * not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;

import android.util.Log;

import com.hoccer.data.GenericStreamableContent;
import com.hoccer.data.StreamableContent;
import com.hoccer.http.AsyncHttpGet;
import com.hoccer.http.AsyncHttpPut;
import com.hoccer.http.AsyncHttpRequest;
import com.hoccer.http.HttpResponseHandler;

public class FileCache extends CloudService {

    private final HashMap<String, AsyncHttpRequest> mOngoingRequests = new HashMap<String, AsyncHttpRequest>();

    public FileCache(ClientConfig config) {
        super(config);
    }

    public HashMap<String, AsyncHttpRequest> getOngoingRequests() {

        synchronized (mOngoingRequests) {
            for (String uri : mOngoingRequests.keySet()) {
                if (mOngoingRequests.get(uri).isTaskCompleted()) {
                    mOngoingRequests.remove(uri);
                }
            }
        }

        return mOngoingRequests;
    }

    public String store(StreamableContent data, int secondsUntilExipred)
            throws ClientProtocolException, IOException, Exception {

        String url = ClientConfig.getFileCacheBaseUri() + "/" + UUID.randomUUID() + "/?expires_in="
                + secondsUntilExipred;
        Log.v("Filecache store()", "put uri = " + url);
        HttpPut request = new HttpPut(sign(url));

        InputStreamEntity entity = new InputStreamEntity(data.openNewInputStream(),
                data.getNewStreamLength());
        request.addHeader("Content-Disposition", " attachment; filename=\"" + data.getFilename()
                + "\"");
        entity.setContentType(data.getContentType());
        request.setEntity(entity);
        request.addHeader("Content-Type", data.getContentType());

        HttpResponse response = getHttpClient().execute(request);

        String responseString = convertResponseToString(response);
        Log.v("Filecache store()", "response = " + responseString);

        return responseString;
    }

    public void fetch(String locationUri, StreamableContent data) throws ClientProtocolException,
            IOException, Exception {
        HttpGet request = new HttpGet(locationUri);
        HttpResponse response = getHttpClient().execute(request);

        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode != 200) {
            throw new HttpResponseException(statuscode, "Unexpected status code " + statuscode
                    + " while requesting " + locationUri);
        }

        InputStream is = response.getEntity().getContent();
        OutputStream storageStream = data.openNewOutputStream();
        byte[] buffer = new byte[0xFFFF];
        int len;
        while ((len = is.read(buffer)) != -1) {
            storageStream.write(buffer, 0, len);
        }

        if (data instanceof GenericStreamableContent) {
            Header[] headers = response.getAllHeaders();
            HashMap<String, String> headermap = new HashMap<String, String>();
            for (int i = 0; i < headers.length; i++) {
                headermap.put(headers[i].getName(), headers[i].getValue());
            }
            AsyncHttpRequest.setTypeAndFilename((GenericStreamableContent) data, headermap,
                    locationUri);
        }
    }

    public String asyncStore(StreamableContent data, int secondsUntilExipred,
            HttpResponseHandler responseHandler) throws Exception {

        String uri = ClientConfig.getFileCacheBaseUri() + "/" + UUID.randomUUID();
        Log.v("Filecache asyncStore()", "uri = " + uri);

        AsyncHttpPut storeRequest = new AsyncHttpPut(sign(uri + "?expires_in="
                + secondsUntilExipred), getHttpClient());

        if (responseHandler != null) {
            storeRequest.registerResponseHandler(responseHandler);
        }
        storeRequest.setBody(data);
        storeRequest.start();
        synchronized (mOngoingRequests) {
            mOngoingRequests.put(uri, storeRequest);
        }

        return uri;
    }

    // public String asyncStore(StreamableContent data, int secondsUntilExipred,
    // HttpResponseHandler responseHandler) throws IOException {
    //
    // String uri = ClientConfig.getFileCacheBaseUri() + "/" + UUID.randomUUID() + "?expires_in="
    // + secondsUntilExipred;
    // uri = sign(uri);
    // Log.v("Filecache store()", "put uri = " + uri);
    //
    // AsyncHttpPut storeRequest = new AsyncHttpPut(uri, getHttpClient());
    //
    // if (responseHandler != null) {
    // storeRequest.registerResponseHandler(responseHandler);
    // }
    // storeRequest.setBody(data);
    // storeRequest.start();
    // synchronized (mOngoingRequests) {
    // mOngoingRequests.put(uri, storeRequest);
    // }
    //
    // return uri;
    // }

    public void asyncFetch(String uri, StreamableContent sink, HttpResponseHandler responseHandler) {
        Log.v("Filecache asyncFetch()", "uri = " + uri);
        AsyncHttpGet fetchRequest = new AsyncHttpGet(uri, getHttpClient());
        fetchRequest.registerResponseHandler(responseHandler);
        fetchRequest.setStreamableContent(sink);
        fetchRequest.start();
        synchronized (mOngoingRequests) {
            mOngoingRequests.put(uri, fetchRequest);
        }
    }

    public void cancel(String uri) {
        synchronized (mOngoingRequests) {
            AsyncHttpRequest request = mOngoingRequests.remove(uri);
            if (request != null) {
                request.interrupt();
            }
        }
    }

    public boolean isOngoing(String uri) {
        synchronized (mOngoingRequests) {
            return mOngoingRequests.get(uri) == null ? false : true;
        }
    }

    public boolean hasOngoingRequests() {
        synchronized (mOngoingRequests) {
            return mOngoingRequests.size() != 0;
        }
    }
}
