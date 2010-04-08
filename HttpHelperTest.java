package com.artcom.y60.http;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import android.net.Uri;

import com.artcom.y60.Logger;

public class HttpHelperTest extends HttpTestCase {
    
    private static final String LOG_TAG = "HttpHelperTest";
    
    public void test404() throws Exception {
        
        try {
            HttpResponse response = HttpHelper.get(getServer().getUri() + "/not-existing");
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
        assertEquals(Uri.parse(uri).toString(), URI.create(uri).toString());
    }
    
    private void assertNotParsable(String uri) {
        boolean parsable = true;
        try {
            assertEquals(Uri.parse(uri).toString(), URI.create(uri).toString());
        } catch (IllegalArgumentException e) {
            parsable = false;
        }
        assertFalse(uri + "should not be parsable", parsable);
    }
}
