/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.playground;

import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

public class WaitingForContent {

    public static void main(String[] args) {

        try {
            final Linccer linccer = new Linccer(new ClientConfig("Playground Linccer"));
            new Thread(new EnvironmentUpdater(linccer)).start();

            while (true) {
                System.out.println("waiting for content");
                JSONObject payload = linccer.receive("1:n", "waiting=true");
                System.out.println("received " + payload);
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
                    mLinccer.onGpsChanged(52.5157780325, 13.409039925, 1000);
                    Thread.sleep(10 * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
