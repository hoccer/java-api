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

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

public class TestLinccing extends LinccerTestsBase {

    @Test(timeout = 20000)
    public void oneToOneSuccsess() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.115, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

        long time = System.currentTimeMillis();
        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();

        JSONObject receivedPayload = linccerB.receive("1:1");
        System.out.println(System.currentTimeMillis() - time);

        assertNotNull("should have received something", receivedPayload);
        assertTrue("should have received a message", receivedPayload.has("message"));
        assertEquals("hello world", receivedPayload.get("message"));

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();
        assertNotNull("should have shared the message", threadedShare.getResult());
        assertEquals("hello world", threadedShare.getResult().get("message"));

        disconnect(linccerA, linccerB);
    }

    @Test(timeout = 20000)
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

        boolean hadCollision = false;
        try {
            JSONObject receivedPayload = linccerC.receive("1:1");
            assertNull("should not have got the content", receivedPayload);
        } catch (CollidingActionsException e) {
            hadCollision = true;
        }
        assertTrue("should have detected collision", hadCollision);

        threadedShare.join();
        threadedShare.assertCollisionOccured();

        threadedReceive.join();
        threadedReceive.assertCollisionOccured();

        disconnect(linccerA, linccerB, linccerC);
    }

    @Test(timeout = 20000)
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

        disconnect(linccerA, linccerB);
    }

    @Test(timeout = 40000)
    public void oneToManyWithWaitingOption() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        linccerA.onGpsChanged(22.012, 102.11, 130);
        linccerB.onGpsChanged(22.012, 102.11, 1030);

        ThreadedReceive threadedReceive = new ThreadedReceive(linccerA, "1:n", "waiting=true");
        threadedReceive.start();

        Thread.sleep(8 * 1000); // wait long time before actually sharing content

        JSONObject payload = new JSONObject();
        payload.put("message", "hello world");
        JSONObject sharedPayload = linccerB.share("1:n", "waiting=true", payload);

        threadedReceive.join();
        threadedReceive.assertNoExceptionsOccured();
        assertEquals("should have got the payload", payload.toString(), threadedReceive.getResult()
                .toString());

        assertNotNull("should have shared the message", sharedPayload);
        assertEquals("hello world", sharedPayload.get("message"));

        disconnect(linccerA, linccerB);
    }

    private void disconnect(Linccer... linccers) throws UpdateException {
        long startTime = System.currentTimeMillis();
        for (Linccer linccer : linccers) {
            linccer.disconnect();
        }
        long duration = System.currentTimeMillis() - startTime;
        assertTrue("disconnecting should not take longer than 1 sec but took " + duration / 1000.0
                + " sec", duration < 1000);

    }
}
