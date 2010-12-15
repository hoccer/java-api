package com.hoccer.playground;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

public class PassHoccerCompatibleBookmark {

    public static void main(String[] args) {

        String content = "{type: 'text/uri-list', content: 'http://hoccer.com/'}";

        try {
            final Linccer linccer = new Linccer(new ClientConfig("Simple Hoccer Bookmark Drag Out",
                    UUID.fromString("452eaa4f-640e-4779-aad5-57bae107edd8")));

            linccer.onGpsChanged(52.5167780325, 13.409039925, 10000);

            JSONObject payload = new JSONObject();
            payload.put("sender", new JSONObject("{client-id: '"
                    + linccer.getClientConfig().getClientId() + "'}"));
            payload.put("data", new JSONArray("[" + content + "]"));

            JSONObject result = linccer.share("1:1", payload);
            if (result == null)
                System.out.println("no one received");
            else
                System.out.println("shared " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
