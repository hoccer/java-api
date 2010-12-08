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

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientConfig {

    private final static String mLinccerUri   = "https://linccer.beta.hoccer.com";
    private final static String mFileCacheUri = "https://filecache.sandbox.hoccer.com";
    private final String        mApplicationName;
    private final UUID          mClientId;

    public ClientConfig(String applicatioName) {
        mApplicationName = applicatioName;
        mClientId = UUID.randomUUID();
    }

    public ClientConfig(String applicatioName, UUID clientId) {
        mApplicationName = applicatioName;
        mClientId = clientId;
    }

    public static String getLinccerBaseUri() {
        return mLinccerUri;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public String getClientUri() {
        return ClientConfig.getLinccerBaseUri() + "/clients/" + getClientId();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("application", getApplicationName());
        return json;
    }

    public String getApiKey() {
        return "115745c0d609012d2f4e001ec2be2ed9";
    }

    public String getSharedSecret() {
        return "DNonxFIWCxS3kHgC9oVG+lUz/60=";
    }

    public UUID getClientId() {
        return mClientId;
    }

    public static String getFileCacheBaseUri() {
        return mFileCacheUri;
    }

}
