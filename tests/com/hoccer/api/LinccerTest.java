package com.hoccer.api;

import org.json.*;

public class LinccerTest {

    public LinccerTest() {
        super();
    }

    protected ClientDescription createDescription() {
        return new ClientDescription("java-api unit test");
    }

    protected class ThreadedLinccing extends Thread {
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

        public void assertCollisionOccured() throws Exception {
            if (!(mException instanceof CollidingActionsException)) {
                throw new AssertionError("no collsion was detected");
            }
        }
    }

    protected class ThreadedShare extends ThreadedLinccing {

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

    protected class ThreadedReceive extends ThreadedLinccing {

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
