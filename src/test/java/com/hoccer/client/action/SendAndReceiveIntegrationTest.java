package com.hoccer.client.action;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.hoccer.client.HoccerClient;
import com.hoccer.client.TestUtil;
import com.hoccer.client.action.Action.Mode;

public class SendAndReceiveIntegrationTest extends TestCase {

    public void testOneToOne() throws Exception {
        
        HoccerClient sender = TestUtil.startClient("sender");
        String msg = "The owls are not what they seem.";
        final boolean[] sendSucceeded = new boolean[]{ false };
        SendAction send = new SendAction(msg, Mode.ONE_TO_ONE, new SendListenerStub() {
            public void sendSucceeded(SendAction pAction) {
                
                sendSucceeded[0] = true;
            }
        });
        
        HoccerClient receiver = TestUtil.startClient("receiver");
        final boolean[] receiveSucceeded = new boolean[]{ false };
        ReceiveAction receive = new ReceiveAction(Mode.ONE_TO_ONE, new ReceiveListenerStub() {
            public void receiveSucceeded(JSONObject pResult, ReceiveAction pAction) {
                
                System.out.println("Received: " + pResult);
                receiveSucceeded[0] = true;
            }
        });
        
        TestUtil.triggerGrouping(sender, receiver);

        Thread.sleep(1000);

        assertEquals("Number of peers", 1, sender.getPeers().size());
        assertEquals("Number of peers", 1, receiver.getPeers().size());

        sender.perform(send);
        receiver.perform(receive);

        Thread.sleep(800);

        assertTrue("Expected send to succeed", sendSucceeded[0]);
        assertTrue("Expected receive to succeed", receiveSucceeded[0]);
    }

    // Inner Classes -----------------------------------------------------

    private class ActionListenerStub<A extends Action> implements ActionListener<A> {

        @Override
        public void actionCollided(A pAction) {
        }

        @Override
        public void actionFailed(A pAction) {
        }

        @Override
        public void actionExpired(A pAction) {
        }

        @Override
        public void actionAborted(A pAction) {
        }
    }

    private class SendListenerStub extends ActionListenerStub<SendAction> implements SendListener {

        @Override
        public void sendSucceeded(SendAction pAction) {
        }
    }

    private class ReceiveListenerStub extends ActionListenerStub<ReceiveAction> implements ReceiveListener {

        @Override
        public void receiveSucceeded(JSONObject pResult, ReceiveAction pAction) {
        }
    }
}
