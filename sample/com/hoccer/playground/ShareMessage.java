package com.hoccer.playground;

import org.json.*;

import com.hoccer.api.*;

public class ShareMessage {

    public static void main(String[] args) {

        try {
            final Linccer linccer = new Linccer(new ClientDescription("PlaygroundLinccer"));
            linccer.onGpsChanged(52.5167780325, 13.409039925, 1000);

            System.out.println("sharing content");
            JSONObject payload = linccer.share("1:n", new JSONObject("{message : 'hello world'}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
