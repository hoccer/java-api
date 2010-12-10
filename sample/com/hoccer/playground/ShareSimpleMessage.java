package com.hoccer.playground;

import java.util.Date;

import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

public class ShareSimpleMessage {

    public static void main(String[] args) {

        try {
            final Linccer linccer = new Linccer(new ClientConfig("PlaygroundLinccer"));
            linccer.onGpsChanged(52.5157780325, 13.409039925, 1000);

            JSONObject payload = linccer.share("1:n", new JSONObject(
                    "{message : 'hello world', timestamp : '" + (new Date()).getTime() + "'}"));
            System.out.println("shared content " + payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}