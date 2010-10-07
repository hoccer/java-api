package com.hoccer.api;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

public class TestLinccing {

    private final ClientDescription description = new ClientDescription("java-api unit test");

    @Test
    public void oneToOne() throws Exception {
        final Linccer linccerA = new Linccer(description);
        Linccer linccerB = new Linccer(description);

        linccerA.onGpsChanged(22.011, 102.113, 130);
        linccerB.onGpsChanged(22.011, 102.113, 1030);

        JSONObject payload = new JSONObject();
        payload.put("message", "hello world");
        JSONObject shareStatus = linccerA.share("1:1", payload);
        assertNotNull("should have shared the message", shareStatus);
        assertEquals("unknown sate", shareStatus.toString());

        JSONObject receivedPayload = linccerB.receive("1:1");
        assertNotNull("should have received something", receivedPayload);
        assertTrue("should have received a message", receivedPayload.has("message"));
        assertEquals("hello world", receivedPayload.get("message"));
    }
}