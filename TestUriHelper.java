package com.artcom.y60.http;

import java.net.URISyntaxException;

import android.net.Uri;

public class TestUriHelper {

    // Constants ---------------------------------------------------------

    public static final String BASE_URI = "http://www.android.com/images/android2-logo.gif";

    // Static Methods ----------------------------------------------------

    public static Uri createUri() throws URISyntaxException {

        return Uri.parse(BASE_URI);
        // return Uri.parse(BASE_URI + "?timestamp=" + System.currentTimeMillis());
    }

}
