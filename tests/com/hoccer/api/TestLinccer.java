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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.json.*;
import org.junit.*;
import org.junit.Test;

public class TestLinccer {
    private ClientDescription description;

    @Before
    public void setUp() {
        description = new ClientDescription("java-api unit test");
    }

    @Test
    public void creatingNewLinccer() throws Exception {
        Linccer linccer = new Linccer(description);
        assertEquals("client id " + linccer.getId() + " should have a sh1 key length", 32, linccer
                .getId().length());
    }

    @Test
    public void creatingMultipleLinccers() throws Exception {
        Linccer a = new Linccer(description);
        Linccer b = new Linccer(description);
        assertThat("two linccers should have different id's", a.getId(), not(equalTo(b.getId())));
    }

    @Test
    public void usingKnownLinccer() throws Exception {
        Linccer linccer = new Linccer(description);
        description.setClientId(linccer.getId());
        Linccer reusedLinccer = new Linccer(description);

        assertThat("reused linker should have same id", reusedLinccer.getId(), is(equalTo(linccer
                .getId())));
    }

    @Test(expected = ClientCreationException.class)
    public void usingWronglyKnownLinccer() throws Exception {
        description.setClientId("2f341030-d2bc-11df-bd3b-0800200c9a66");
        Linccer linccer = new Linccer(description);
    }

    @Test
    public void sendingGpsData() throws Exception {
        Linccer linccer = new Linccer(description);
        linccer.onGpsChanged(22.011, 102.113, 130);
    }

    @Test
    public void sharingWithoutEnvironment() throws Exception {
        Linccer client = new Linccer(description);
        JSONObject payload = new JSONObject();
        payload.put("demo_key", "demo_value");
        assertNull("should not succsess with transfer", client.share("1:1", payload));
    }

    @Test(expected = BadModeException.class)
    public void sharingWithUnmappableMode() throws Exception {
        Linccer client = new Linccer(description);
        JSONObject payload = new JSONObject();
        payload.put("demo_key", "demo_value");
        client.share("no:mode", payload);
    }

    @Test
    public void receivingWithoutEnvironment() throws Exception {
        Linccer linccer = new Linccer(description);
        assertNull("should not succsess with transfer", linccer.receive("1:1"));
    }
}
