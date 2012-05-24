package com.hoccer.client;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.UpdateException;

public class TestUtil {

    /** Creates and starts a new HoccerClient instance using the given name as client name. */
    public static HoccerClient startClient(String pName) {
    
        HoccerClient client = new HoccerClient();
        client.configure(new ClientConfig("Java API Tests"));
        client.setClientName(pName);
        client.start();
    
        return client;
    }

    /** Puts the given client instances at the same random geolocation and triggers environment submit for each */
    public static void triggerGrouping(HoccerClient... pClients) throws ClientProtocolException, UpdateException,
            IOException {

        double lat = Math.random();
        double lon = Math.random();

        for (HoccerClient client : pClients) {

            client.getLinker().onGpsChanged(lat, lon, 1);
            client.triggerSubmitter();
        }
    }

}
