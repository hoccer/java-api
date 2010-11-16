package com.hoccer.playground;

import org.json.*;

import com.hoccer.api.*;

public class LinccerPlayground {

    public static void main(String[] args) {

        try {
            final Linccer linccer = new Linccer(new ClientDescription("PlaygroundLinccer"));
            new Thread(new EnvironmentUpdater(linccer)).start();

            while (true) {
                System.out.println("waiting for content");
                JSONObject received = linccer.receive("1:n", "waiting=true");
                System.out.println(received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EnvironmentUpdater implements Runnable {
        Linccer mLinccer;

        public EnvironmentUpdater(Linccer linccer) {
            mLinccer = linccer;
        }

        public void run() {
            try {
                while (true) {
                    mLinccer.onGpsChanged(52.5167780325, 13.409039925, 1000);
                    Thread.sleep(10 * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
