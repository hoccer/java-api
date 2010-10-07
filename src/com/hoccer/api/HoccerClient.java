/*
 *  Copyright (C) 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 *  These coded instructions, statements, and computer programs contain
 *  proprietary information of Hoccer GmbH Berlin, and are copy protected
 *  by law. They may be used, modified and redistributed under the terms
 *  of GNU General Public License referenced below. 
 *     
 *  Alternative licensing without the obligations of the GPL is
 *  available upon request.
 * 
 *  GPL v3 Licensing:
 * 
 *  This file is part of the "Hoccer Java-API".
 * 
 *  Hoccer Java-API is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Hoccer Java-API is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Hoccer Java-API. If not, see <http: * www.gnu.org/licenses/>.
 */
package com.hoccer.api;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import org.json.JSONException;
import org.json.JSONObject;

public class HoccerClient {

    private DefaultHttpClient  mHttpClient;
    private final ClientConfig mConfig;
    private final String       mClientUri;

    public HoccerClient(ClientConfig config) throws ClientProtocolException, IOException,
            ParseException, JSONException, UpdateException {
        mConfig = config;
        setupHttpClient();

        HttpPost clientCreationRequest = new HttpPost(ClientConfig.getRemoteServer() + "/clients");
        clientCreationRequest.setEntity(new StringEntity(mConfig.toJson().toString()));

        mClientUri = ClientConfig.getRemoteServer()
                + convert(mHttpClient.execute(clientCreationRequest)).getString("uri");
    }

    public void onGpsMeasurement(double latitude, double longitude, int accuracy)
            throws UpdateException {
        HttpResponse response;
        try {
            HttpPut request = new HttpPut(mClientUri + "/environment/gps");
            JSONObject gps = new JSONObject();
            gps.put("latitude", latitude);
            gps.put("longitude", longitude);
            gps.put("accuracy", accuracy);
            request.setEntity(new StringEntity(gps.toString()));
            response = mHttpClient.execute(request);
        } catch (Exception e) {
            throw new UpdateException("could not update gps measurement for " + mClientUri
                    + " because of " + e);
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new UpdateException("could not update gps measurement for " + mClientUri
                    + " because server responded with status "
                    + response.getStatusLine().getStatusCode());
        }

    }

    public boolean share(String mode, JSONObject payload) throws BadModeException,
            ClientActionException {
        mode = mapMode(mode);

        try {
            HttpPut request = new HttpPut(mClientUri + "/action/" + mode);
            request.setEntity(new StringEntity(payload.toString()));

            HttpResponse response = mHttpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return false;
            }

        } catch (Exception e) {
            throw new ClientActionException("could not share payload " + payload.toString()
                    + " because of " + e);
        }

        return true;
    }

    public String getId() throws InvalidObjectException {
        try {
            return new URI(mClientUri).getPath().substring(9);
        } catch (URISyntaxException e) {
            throw new InvalidObjectException("the client was not correctly initalized");
        }
    }

    private void setupHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        mHttpClient = new DefaultHttpClient(cm, httpParams);
        mHttpClient.getParams().setParameter("http.useragent", mConfig.getApplicationName());
    }

    private JSONObject convert(HttpResponse response) throws ParseException, IOException,
            JSONException, UpdateException {

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new UpdateException("server respond with status code "
                    + response.getStatusLine().getStatusCode());
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ParseException("http body was empty");
        }
        long len = entity.getContentLength();
        if (len > 2048) {
            throw new ParseException(
                    "http body is to big and must be streamed (max is 2048, but was " + len
                            + " byte)");
        }

        String body = EntityUtils.toString(entity);
        return new JSONObject(body);
    }

    private String mapMode(String mode) throws BadModeException {
        if (mode.equals("1:1")) {
            return "pass";
        } else if (mode.equals("1:n")) {
            return "distribute";
        } else if (mode.equals("n:n")) {
            return "exchange";
        }

        throw new BadModeException("the provided mode name '" + mode + "' could not be mapped");
    }

}
