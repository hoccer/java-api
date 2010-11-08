package com.hoccer.api;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

public class TestEnvironment extends LinccerTest {

    @Test(timeout = 20000)
    public void gpsTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.115, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

        assertConnectable(linccerA, linccerB);
    }

    @Test(timeout = 20000)
    public void networkTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onNetworkChanged(22.012, 102.115, 130);
        linccerB.onNetworkChanged(22.012, 102.11, 1030);

        assertConnectable(linccerA, linccerB);
    }

    @Test(timeout = 20000)
    public void wifiTest() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onWifiChanged(new String[] { "00:22:3F:11:5A:2E", "4C:12:3F:11:5A:2C" });
        linccerB.onWifiChanged(new String[] { "00:22:3F:11:5A:2E", "5C:12:3F:11:5A:2C" });

        assertConnectable(linccerA, linccerB);
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
}
