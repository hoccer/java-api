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

        linccerA.onGpsChanged(22.012, 102.113, 130);
        linccerB.onGpsChanged(22.011, 102.11, 1030);

        LinccerThread sharingThread = new LinccerThread() {
            JSONObject shareStatus;

            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("message", "hello world");
                    shareStatus = linccerA.share("1:1", payload);
                } catch (Exception e) {
                    exception = e;
                }
            };

            @Override
            public void checkAssertions() throws Exception {
                super.checkAssertions();
                assertNotNull("should have shared the message", shareStatus);
                assertEquals("should have one receiver", 1, shareStatus.get("receiver"));
            }
        };
        sharingThread.start();

        JSONObject receivedPayload = linccerB.receive("1:1");
        assertNotNull("should have received something", receivedPayload);
        assertTrue("should have received a message", receivedPayload.has("message"));
        assertEquals("hello world", receivedPayload.get("message"));

        sharingThread.join();
        sharingThread.checkAssertions();
    }

    class LinccerThread extends Thread {
        protected Exception exception;

        public void checkAssertions() throws Exception {
            if (exception != null) {
                throw exception;
            }
        }
    }
}
