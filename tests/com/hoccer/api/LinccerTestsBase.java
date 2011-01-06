/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
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

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.json.JSONObject;

public class LinccerTestsBase {

    public LinccerTestsBase() {
        super();
    }

    protected ClientConfig createDescription() {
        return new ClientConfig("java-api unit test");
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
            synchronized (mLinccer) {
                return mResult;
            }
        }

        public void assertNoExceptionsOccured() throws Exception {
            synchronized (mLinccer) {
                if (mException != null) {
                    throw mException;
                }
            }
        }

        public void assertCollisionOccured() throws Exception {
            synchronized (mLinccer) {
                if (!(mException instanceof CollidingActionsException)) {
                    throw new AssertionError("no collsion was detected for " + mLinccer.getUri());
                }
            }
        }
    }

    protected class ThreadedShare extends ThreadedLinccing {

        public ThreadedShare(Linccer linccer, String mode) {
            super(linccer, mode);
        }

        @Override
        public void run() {
            synchronized (getLinccer()) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("message", "hello world");
                    mResult = getLinccer().share(mMode, payload);
                } catch (Exception e) {
                    mException = e;
                }
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
            synchronized (getLinccer()) {
                try {
                    mResult = getLinccer().receive(mMode, mOptions);
                } catch (Exception e) {
                    mException = e;
                }
            }
        }
    }

    static void placeNearBy(Linccer... linccers) throws UpdateException {
        Random rand = new Random(System.currentTimeMillis());

        double latitude = 22.012 + rand.nextGaussian() * 10;
        double longitude = 102.112 + rand.nextGaussian() * 10;

        for (Linccer linccer : linccers) {
            linccer.onGpsChanged(latitude + (rand.nextGaussian() / 1000.0), longitude
                    + (rand.nextGaussian() / 1000.0), rand.nextInt(1000));
        }
    }

    static void disconnect(Linccer... linccers) throws UpdateException {
        long startTime = System.currentTimeMillis();
        for (Linccer linccer : linccers) {
            linccer.disconnect();
        }
        long duration = System.currentTimeMillis() - startTime;
        assertTrue("disconnecting should not take longer than 1 sec but took " + duration / 1000.0
                + " sec", duration < 1000);

    }
}
