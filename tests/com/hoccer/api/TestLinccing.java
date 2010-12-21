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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.json.JSONObject;
import org.junit.Test;

public class TestLinccing extends LinccerTestsBase {

    @Test(timeout = 20000)
    public void oneToOneSuccsess() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        Linccer linccerB = new Linccer(createDescription());

        placeNearBy(linccerA, linccerB);

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

        disconnect(linccerA, linccerB);
    }

    @Test(timeout = 6000)
    public void oneToOneSuccsessWithThreeClients() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        placeNearBy(linccerA, linccerB, linccerC);

        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();

        long startTime = System.currentTimeMillis();
        assertNotNull("should have got the content", linccerC.receive("1:1"));
        double duration = (System.currentTimeMillis() - startTime) / 1000;
        assertTrue("should have took about 2 seconds but took " + duration + " sec",
                duration > 1.95 && duration < 2.05);

        threadedShare.join();
        threadedShare.assertNoExceptionsOccured();

        disconnect(linccerA, linccerB, linccerC);
    }

    @Test(timeout = 20000)
    public void oneToOneReceiveCollision() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        placeNearBy(linccerA, linccerB, linccerC);

        ThreadedShare threadedShare = new ThreadedShare(linccerA, "1:1");
        threadedShare.start();
        ThreadedReceive threadedReceive = new ThreadedReceive(linccerB, "1:1");
        threadedReceive.start();

        boolean hadCollision = false;
        try {
            linccerC.receive("1:1");
            assertFalse("should have got an exception", true);
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
    public void oneToOneShareCollision() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        placeNearBy(linccerA, linccerB, linccerC);

        ThreadedShare threadedShareA = new ThreadedShare(linccerA, "1:1");
        threadedShareA.start();
        ThreadedShare threadedShareB = new ThreadedShare(linccerB, "1:1");
        threadedShareB.start();

        boolean hadCollision = false;
        try {
            linccerC.receive("1:1");
            assertFalse("should have got an exception", true);
        } catch (CollidingActionsException e) {
            hadCollision = true;
        }
        assertTrue("should have detected collision", hadCollision);

        threadedShareA.join();
        threadedShareA.assertCollisionOccured();

        threadedShareB.join();
        threadedShareB.assertCollisionOccured();

        disconnect(linccerA, linccerB, linccerC);
    }

    @Test(timeout = 20000)
    public void oneToManySuccess() throws Exception {
        final Linccer linccerA = new Linccer(createDescription());
        final Linccer linccerB = new Linccer(createDescription());
        final Linccer linccerC = new Linccer(createDescription());

        placeNearBy(linccerA, linccerB, linccerC);

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

        placeNearBy(linccerA, linccerB);

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

    

}
