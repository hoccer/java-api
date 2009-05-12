package com.artcom.y60.http;

import java.net.URI;
import java.net.URISyntaxException;

public class TestUriHelper {
    
    // Constants ---------------------------------------------------------

    public static final String BASE_URI = "http://www.artcom.de/templates/artcom/css/images/artcom_rgb_screen_193x22.png";
    
    
    
    // Static Methods ----------------------------------------------------

    public static URI createUri() throws URISyntaxException {
        
        return new URI(BASE_URI+"?timestamp="+System.currentTimeMillis());
    }

}
