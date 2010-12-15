package com.hoccer.playground;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

public class PassHoccerCompatibleBookmark {

    public static void main(String[] args) {

        // String content = "{type: 'text/uri-list', content: 'http://hoccer.com/'}";
        // String content =
        // "{type: 'image/jpeg', uri: 'http://hoccer.com/wp-content/themes/hoccer/images/logo.jpg'}";
        String content = "{type: 'audio/ogg', uri: 'http://filecache.beta.hoccer.com/41c46a9e-0a75-4690-8cb7-9779909c91c7'}";
        // String content =
        // "{type: 'image/jpeg', uri: 'http://filecache.beta.hoccer.com/1122ffaa-8c99-49ee-b045-33d737cc50f9'}";

        try {
            final Linccer linccer = new Linccer(new ClientConfig("Simple Hoccer Bookmark Passer",
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
