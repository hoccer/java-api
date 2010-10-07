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

import junit.framework.TestCase;

import org.json.JSONObject;

public class TestHoccerClient extends TestCase {
    private final ClientConfig description = new ClientConfig("java-api unit test");

    public void testCreatingNewClient() throws Exception {
        HoccerClient client = new HoccerClient(description);
        assertEquals("client id " + client.getId() + " should have a sh1 key length", 32, client
                .getId().length());
    }

    public void testSendingGpsData() throws Exception {
        HoccerClient client = new HoccerClient(description);
        client.onGpsMeasurement(22.011, 102.113, 130);
    }

    public void testSharingWithoutEnvironment() throws Exception {
        HoccerClient client = new HoccerClient(description);
        JSONObject payload = new JSONObject();
        payload.put("demo_key", "demo_value");
        assertFalse("should not succsess with transfer", client.share("1:1", payload));
    }
}
