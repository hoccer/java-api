package com.artcom.y60.http;

import java.net.URISyntaxException;

import android.net.Uri;

public class TestUriHelper {
    
    // Constants ---------------------------------------------------------

    public static final String BASE_URI = "http://www.artcom.de/templates/artcom/css/images/artcom_rgb_screen_193x22.png";
    
    
    
    // Static Methods ----------------------------------------------------

    public static Uri createUri() throws URISyntaxException {
        
        return Uri.parse(BASE_URI+"?timestamp="+System.currentTimeMillis());
    }

}
