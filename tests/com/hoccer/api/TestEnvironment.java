/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Linccer GmbH Berlin, and are copy protected
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
package com.hoccer.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class TestEnvironment extends LinccerTestsBase {

    @Test(timeout = 20000)
    public void gpsTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.115, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

        assertConnectable(linccerA, linccerB);

        linccerA.disconnect();
        linccerB.disconnect();
    }

    @Test(timeout = 20000)
    public void gpsNotMatchingTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(24.012, 102.115, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

        assertNotConnectable(linccerA, linccerB);

        linccerA.disconnect();
        linccerB.disconnect();
    }

    @Test(timeout = 20000)
    public void networkTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onNetworkChanged(22.012, 102.115, 130);
        linccerB.onNetworkChanged(22.012, 102.11, 1030);

        assertConnectable(linccerA, linccerB);

        linccerA.disconnect();
        linccerB.disconnect();
    }

    @Test(timeout = 20000)
    public void networkNotMatchingTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onNetworkChanged(24.012, 105.14, 130);
        linccerB.onNetworkChanged(22.012, 102.11, 1030);

        assertNotConnectable(linccerA, linccerB);
    }

    @Test(timeout = 20000)
    public void wifiTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onWifiChanged(new String[] { "00:22:3F:11:5A:2E", "4C:12:3F:11:5A:2C" });
        linccerB.onWifiChanged(new String[] { "00:22:3F:11:5A:2E", "5C:12:3F:11:5A:2C" });

        assertConnectable(linccerA, linccerB);

        linccerA.disconnect();
        linccerB.disconnect();
    }

    @Test(timeout = 20000)
    public void wifiNotMatchingTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onWifiChanged(new String[] { "00:22:3F:11:5A:2E", "4C:12:3F:11:5A:2C" });
        linccerB.onWifiChanged(new String[] { "10:22:3F:11:5A:2E", "5C:12:3F:11:5A:2C" });

        assertNotConnectable(linccerA, linccerB);

        linccerA.disconnect();
        linccerB.disconnect();
    }

    private void assertConnectable(final Linccer linccerA, Linccer linccerB)
            throws BadModeException, ClientActionException, CollidingActionsException,
            JSONException, InterruptedException, Exception {
        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();

        JSONObject receivedPayload = linccerB.receive("1:1");
        assertNotNull("should have received something", receivedPayload);
        assertTrue("should have received a message", receivedPayload.has("message"));
        assertEquals("hello world", receivedPayload.get("message"));

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();
        assertNotNull("should have shared the message", threadedShare.getResult());
        assertEquals("hello world", threadedShare.getResult().get("message"));
    }

    private void assertNotConnectable(final Linccer linccerA, Linccer linccerB)
            throws BadModeException, ClientActionException, CollidingActionsException,
            JSONException, InterruptedException, Exception {
        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();

        JSONObject receivedPayload = linccerB.receive("1:1");
        assertNull("should not have received something", receivedPayload);

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();
        assertNull("should not have shared the message", threadedShare.getResult());
    }
}
