package com.hoccer.api;

import org.json.*;

public class LinccerTestsBase {

    public LinccerTestsBase() {
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
        protected final String mOptions;

        public ThreadedLinccing(Linccer linccer, String mode) {
            mLinccer = linccer;
            mMode = mode;
            mOptions = "";
        }

        public ThreadedLinccing(Linccer linccer, String mode, String options) {
            mLinccer = linccer;
            mMode = mode;
            mOptions = options;
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
            System.out.println("sharing");

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

        public ThreadedReceive(Linccer linccer, String mode, String options) {
            super(linccer, mode, options);
        }

        @Override
        public void run() {
            System.out.println("receiving");

            try {
                mResult = getLinccer().receive(mMode, mOptions);
            } catch (Exception e) {
                mException = e;
            }
        };
    }
}
