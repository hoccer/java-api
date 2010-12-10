package com.hoccer.playground;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

public class PassHoccerCompatibleBookmark {

    public static void main(String[] args) {

        // String content = "{type: 'text/uri-list', content: 'http://hoccer.com/'}";
        String content = "{type: 'image/jpeg', uri: 'http://hoccer.com/wp-content/themes/hoccer/images/logo.jpg'}";

        try {
            final Linccer linccer = new Linccer(new ClientConfig("Simple Hoccer Bookmark Passer"));
            linccer.onGpsChanged(52.5167780325, 13.409039925, 10000);

            JSONObject payload = new JSONObject();
            payload.put("sender", new JSONObject("{client-id: '"
                    + linccer.getClientConfig().getClientId() + "'}"));
            payload.put("data", new JSONArray("[" + content + "]"));

            JSONObject result = linccer.share("1:n", payload);
            if (result == null)
                System.out.println("no one received");
            else
                System.out.println("shared " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
