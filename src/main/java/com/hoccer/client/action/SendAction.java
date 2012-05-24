package com.hoccer.client.action;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.api.BadModeException;
import com.hoccer.api.ClientActionException;
import com.hoccer.api.CollidingActionsException;
import com.hoccer.api.Linccer;
import com.hoccer.util.HoccerLoggers;

/** Implementation of an action for sending data */
public class SendAction extends Action<SendListener> {

    // Constants ---------------------------------------------------------

    private static final String LOG_TAG = SendAction.class.getSimpleName();
    private static final Logger LOG = HoccerLoggers.getLogger(LOG_TAG);

    // Instance Fields ---------------------------------------------------

    /** The content that is to be sent (inline) */
    private String mContent;

    // Constructors ------------------------------------------------------

    public SendAction(String pContent, Mode pMode, SendListener pListener) {

        super(Type.SEND, pMode, pListener);

        mContent = pContent;
    }

    // Public Instance Methods -------------------------------------------

    /** Perform this send action against the given Linccer */
    @Override
    public void perform(Linccer pLinker) {

        JSONObject result = null;

        try {
            
            JSONObject payload = new JSONObject();

            JSONObject sender = new JSONObject();
            String clientId = pLinker.getClientConfig().getClientId().toString();
            sender.put("client-id", clientId);
            payload.put("sender", sender);

            JSONArray data = new JSONArray();
            JSONObject dataItem = new JSONObject();
            dataItem.put("content", mContent);
            dataItem.put("type", "text/plain");
            data.put(dataItem);
            payload.put("data", data);

            result = pLinker.share(getModeString(), payload);

        } catch (JSONException e) {
            onActionFailed();
            e.printStackTrace();
        } catch (BadModeException e) {
            onActionFailed();
            e.printStackTrace();
        } catch (ClientActionException e) {
            onActionFailed();
            e.printStackTrace();
        } catch (CollidingActionsException e) {
            onActionCollided();
            e.printStackTrace();
        }

        if (result == null) {
            LOG.info("Send expired");
            onActionExpired();
        } else {
            LOG.info("Send succeeded: " + result.toString());
            getActionListener().sendSucceeded(this);
        }

    }

}
