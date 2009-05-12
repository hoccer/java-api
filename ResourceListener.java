package com.artcom.y60.http;

import java.net.URI;

import android.net.Uri;

public interface ResourceListener {

    public void onResourceChanged(URI pResourceUri);
    public void onResourceAvailable(Uri pResourceUri);
}
