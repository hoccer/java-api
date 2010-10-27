package com.hoccer.api;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

public class TestLinccing {

    private ClientDescription createDescription() {
        return new ClientDescription("java-api unit test");
    }

    @Test(timeout = 8000)
    public void oneToOneSuccsess() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.115, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

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

    @Test(timeout = 8000)
    public void oneToOneCollision() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.113, 130);
        linccerB.onGpsChanged(22.011, 102.11, 1030);
        linccerC.onGpsChanged(22.009, 102.116, 2000);

        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();
        ThreadedReceive threadedReceive = new ThreadedReceive(linccerB, "1:1");
        threadedReceive.start();

        JSONObject receivedPayload = linccerC.receive("1:1");
        assertNull("should not have got the content", receivedPayload);

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();
        assertNull("should not have got the content", threadedShare.getResult());

        threadedReceive.join();
        threadedReceive.assertNoExceptionsOccured();
        assertNotNull("should not have got the content", threadedReceive.getResult());
    }

    @Test(timeout = 8000)
    public void oneToManySuccess() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.113, 130);
        linccerB.onGpsChanged(22.011, 102.11, 1030);
        linccerC.onGpsChanged(22.009, 102.116, 2000);

        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:n");
        threadedShare.start();
        ThreadedReceive threadedReceive = new ThreadedReceive(linccerB, "1:n");
        threadedReceive.start();

        JSONObject receivedPayload = linccerC.receive("1:n");
        assertNotNull("should have received something", receivedPayload);
        assertTrue("should have received a message", receivedPayload.has("message"));
        assertEquals("hello world", receivedPayload.get("message"));

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();
        assertEquals("should have got the payload", receivedPayload.toString(), threadedShare
                .getResult().toString());

        threadedReceive.join();
        threadedReceive.assertNoExceptionsOccured();
        assertEquals("should also have got the payload", receivedPayload.toString(),
                threadedReceive.getResult().toString());
    }

    class ThreadedLinccing extends Thread {
        protected Exception    mException;
        private final Linccer  mLinccer;
        JSONObject             mResult;
        protected final String mMode;

        public ThreadedLinccing(Linccer linccer, String mode) {
            mLinccer = linccer;
            mMode = mode;
        }

        public Linccer getLinccer() {
            return mLinccer;
        }

        public JSONObject getResult() {
            return mResult;
        }

        public void assertNoExceptionsOccured() throws Exception {
            if (mException != null) {
                throw mException;
            }
        }
    }

    class ThreadedShare extends ThreadedLinccing {

        public ThreadedShare(Linccer linccer, String mode) {
            super(linccer, mode);
        }

        @Override
        public void run() {
            try {
                JSONObject payload = new JSONObject();
                payload.put("message", "hello world");
                mResult = getLinccer().share(mMode, payload);
            } catch (Exception e) {
                mException = e;
            }
        };
    }

    class ThreadedReceive extends ThreadedLinccing {

        public ThreadedReceive(Linccer linccer, String mode) {
            super(linccer, mode);
        }

        @Override
        public void run() {
            try {
                mResult = getLinccer().receive(mMode);
            } catch (Exception e) {
                mException = e;
            }
        };
    }
}
