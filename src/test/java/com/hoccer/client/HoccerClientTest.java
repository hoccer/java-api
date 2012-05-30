package com.hoccer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import com.hoccer.client.HoccerClient.PeerListener;
import com.hoccer.util.HoccerLoggers;

public class HoccerClientTest extends TestCase {

    // Public Instance Methods -------------------------------------------

    public void testBasicLifecycle() throws InterruptedException {

        HoccerClient client = TestUtil.startClient(getName());

        Thread.sleep(200);
        client.triggerSubmitter();
        Thread.sleep(200);

        client.stop();
    }

//    public void testSubmitAndPeek() throws Exception {
//
//        HoccerLoggers.getLogger(Peeker.class).setLevel(Level.FINEST);
//        Logger l = HoccerLoggers.getLogger(Submitter.class);
//
//        l.setLevel(Level.FINEST);
//
//        l.info("infoooooooooooo");
//        l.fine("fiiiiiiiiiiiiiiiine");
//        l.finer("fiiiiiiiiiiiiiiiiiiiiiiiiner");
//        l.finest("fiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiinest");
//
//        final HoccerClient client1 = TestUtil.startClient("client1");
//        final HoccerClient client2 = TestUtil.startClient("client2");
//
//        final boolean[] has1Seen2 = new boolean[]{ false };
//        client1.registerPeerListener(new PeerListener() {
//            
//            @Override
//            public void peerUpdated(HoccerPeer pPeer) {
//                
//                if (client2.getClientName().equals(pPeer.getName())) {
//                    
//                    has1Seen2[0] = true;
//                }
//            }
//            
//            @Override
//            public void peerRemoved(HoccerPeer pPeer) {
//            }
//            
//            @Override
//            public void peerAdded(HoccerPeer pPeer) {
//
//                peerUpdated(pPeer);
//            }
//        });
//
//        final boolean[] has2Seen1 = new boolean[] { false };
//        client2.registerPeerListener(new PeerListener() {
//
//            @Override
//            public void peerUpdated(HoccerPeer pPeer) {
//
//                if (client1.getClientName().equals(pPeer.getName())) {
//
//                    has2Seen1[0] = true;
//                }
//            }
//
//            @Override
//            public void peerRemoved(HoccerPeer pPeer) {
//            }
//
//            @Override
//            public void peerAdded(HoccerPeer pPeer) {
//
//                peerUpdated(pPeer);
//            }
//        });
//
//        Thread.sleep(200);
//
//        TestUtil.triggerGrouping(client1, client2);
//
//        Thread.sleep(10000);
//
//        assertTrue("Client #1 should have seen #2 as peer", has1Seen2[0]);
//        assertTrue("Client #2 should have seen #1 as peer", has2Seen1[0]);
//    }

}
