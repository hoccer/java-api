package com.artcom.y60.http;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;

import com.artcom.y60.Logger;

public class HttpHelperTest extends TestCase {
    
    private static final String LOG_TAG = "HttpHelperTest";
    
    public void test404() throws Exception {
        
        try {
            HttpResponse response = HttpHelper.get("http://artcom.de/dieseurlgibtsgarnicht");
            Logger.v(LOG_TAG, response.getStatusLine().getStatusCode(), " - ", response
                    .getStatusLine());
            fail("expected a 404 exception!");
            
        } catch (HttpClientException ex) {
            
            assertEquals("expected a 404 exception", 404, ex.getStatusCode());
            Logger.v(LOG_TAG, ex.getMessage());
        }
    }
    
    public void testSimpleUrlEncoding() {
        
        assertEquals("a+funky+test", URLEncoder.encode("a funky test"));
    }
    
    public void testUrlEncodingAHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "A & B");
        map.put("key%", "C & D");
        assertEquals("key%25=C+%26+D&key1=A+%26+B", HttpHelper.urlEncodeKeysAndValues(map));
    }
    
    public void testUrlEncodingTheValuesOfAHashMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key[1]", "A & B");
        map.put("key%", "C & D");
        assertEquals("key[1]=A+%26+B&key%=C+%26+D", HttpHelper.urlEncodeValues(map));
    }
}
