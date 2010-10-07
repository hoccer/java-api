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

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.*;
import org.apache.http.conn.params.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;

public class Linccer {

    private DefaultHttpClient       mHttpClient;
    private final ClientDescription mConfig;
    private final String            mClientUri;
    private Environment             mEnvironment = new Environment();

    public Linccer(ClientDescription config) throws ClientCreationException {
        mConfig = config;
        setupHttpClient();

        HttpPost clientCreationRequest = new HttpPost(ClientDescription.getRemoteServer()
                + "/clients");
        try {
            clientCreationRequest.setEntity(new StringEntity(mConfig.toJson().toString()));

            mClientUri = ClientDescription.getRemoteServer()
                    + convertResponseToJsonObject(mHttpClient.execute(clientCreationRequest))
                            .getString("uri");
        } catch (Exception e) {
            throw new ClientCreationException("could not create linccer client because of " + e);
        }
    }

    private void onEnvironmentChanged(Environment environment) throws UpdateException {
        mEnvironment = environment;

        HttpResponse response;
        try {
            HttpPut request = new HttpPut(mClientUri + "/environment");
            request.setEntity(new StringEntity(mEnvironment.toJson().toString()));
            response = mHttpClient.execute(request);
        } catch (Exception e) {
            throw new UpdateException("could not update gps measurement for " + mClientUri
                    + " because of " + e);
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new UpdateException(
                    "could not update environment because server responded with status "
                            + response.getStatusLine().getStatusCode());
        }
    }

    public void onGpsChanged(double latitude, double longitude, int accuracy)
            throws UpdateException {

        mEnvironment.setGpsMeasurement(latitude, longitude, accuracy, new Date());
        onEnvironmentChanged(mEnvironment);
    }

    public JSONObject share(String mode, JSONObject payload) throws BadModeException,
            ClientActionException {

        mode = mapMode(mode);
        int statusCode;
        try {
            HttpPost request = new HttpPost(mClientUri + "/action/" + mode);
            request.setEntity(new StringEntity(payload.toString()));
            HttpResponse response = mHttpClient.execute(request);

            statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 204:
                    return null;
                case 200:
                    return convertResponseToJsonObject(response);
                default:
                    // handled at the end of the method
            }

        } catch (Exception e) {
            throw new ClientActionException("could not share payload " + payload.toString()
                    + " because of " + e);
        }

        throw new ClientActionException("could not share payload " + payload.toString()
                + " because server responded with status code " + statusCode);

    }

    public JSONArray receive(String mode) throws BadModeException, ClientActionException {

        mode = mapMode(mode);
        int statusCode;

        try {
            HttpGet request = new HttpGet(mClientUri + "/action/" + mode);
            HttpResponse response = mHttpClient.execute(request);

            statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 204:
                    return null;
                case 200:
                    return convertResponseToJsonArray(response);
                default:
                    // handled at the end of the method
            }

        } catch (Exception e) {
            throw new ClientActionException("could not receive payload because of " + e);
        }

        throw new ClientActionException(
                "could not receive payload because server responded with status code " + statusCode);
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

    private JSONObject convertResponseToJsonObject(HttpResponse response) throws ParseException,
            IOException, JSONException, UpdateException {
        String body = convertResponseToString(response);
        return new JSONObject(body);
    }

    private JSONArray convertResponseToJsonArray(HttpResponse response) throws ParseException,
            IOException, JSONException, UpdateException {
        String body = convertResponseToString(response);
        return new JSONArray(body);
    }

    private String convertResponseToString(HttpResponse response) throws UpdateException,
            IOException {
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
        return body;
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
