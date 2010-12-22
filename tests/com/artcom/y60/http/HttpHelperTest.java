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
package com.artcom.y60.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.hoccer.http.AsyncHttpTestCase;
import com.hoccer.tools.HttpClientException;
import com.hoccer.tools.HttpHelper;

public class HttpHelperTest extends AsyncHttpTestCase {

    private static final String LOG_TAG = "HttpHelperTest";

    public void test404() throws Exception {

        try {
            HttpResponse response = HttpHelper.get(getServer().getUri() + "/not-existing");
            fail("expected a 404 exception!");
        } catch (HttpClientException ex) {
            assertEquals("expected a 404 exception", 404, ex.getStatusCode());
        }
    }

    public void testSimpleUrlEncoding() {
        assertEquals("a+funky+test", URLEncoder.encode("a funky test"));
    }

    public void testUrlEncodingAHashMap() throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "A & B");
        map.put("key%", "C & D");

        String encoded = HttpHelper.urlEncodeKeysAndValues(map);
        String[] keyValuePairs = encoded.split("&");
        Map<String, String> decodedMap = new HashMap<String, String>();
        int counter;
        for (counter = 0; counter < keyValuePairs.length; counter++) {
            String decoded = URLDecoder.decode(keyValuePairs[counter], "UTF-8");
            String[] keyValuePair;
            keyValuePair = decoded.split("=");
            decodedMap.put(keyValuePair[0], keyValuePair[1]);
        }
        assertEquals("A & B", decodedMap.get("key1"));
        assertEquals("C & D", decodedMap.get("key%"));
    }

    public void testUrlEncodingTheValuesOfAHashMap() throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key[1]", "A & B");
        map.put("key%", "C & D");

        String encoded = HttpHelper.urlEncodeValues(map);
        String[] keyValuePairs = encoded.split("&");
        Map<String, String> decodedMap = new HashMap<String, String>();
        int counter;
        for (counter = 0; counter < keyValuePairs.length; counter++) {
            String[] keyValuePair = keyValuePairs[counter].split("=");
            keyValuePair[1] = URLDecoder.decode(keyValuePair[1], "UTF-8");
            decodedMap.put(keyValuePair[0], keyValuePair[1]);
        }
        assertEquals("A & B", decodedMap.get("key[1]"));
        assertEquals("C & D", decodedMap.get("key%"));
    }

    public void testParsingSomeUrls() {
        assertParsable("http://localhost:4000");
        assertParsable("http://localhost:4000/test");
        assertParsable("http://localhost:4000/test-heee");
        assertParsable("http://localhost/test?message=hello");
        assertParsable("http://localhost:4000?message=hello");
        assertParsable("http://localhost:4000&message=hello");
        assertNotParsable("http://localhost:4000?message=hello world");
        assertNotParsable("http://%%ocalhost:4000§§ &m  []  = =");
    }

    private void assertParsable(String uri) {
        assertEquals(uri, URI.create(uri).toString());
    }

    private void assertNotParsable(String uri) {
        boolean parsable = true;
        try {
            assertEquals(uri, URI.create(uri).toString());
        } catch (IllegalArgumentException e) {
            parsable = false;
        }
        assertFalse(uri + "should not be parsable", parsable);
    }
}
