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

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;

/*
 * Beware, this demo will only work with a future version of the official 
 * Hoccer clients. Stay tuned!
 */
public class PassHoccerCompatibleData {

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
