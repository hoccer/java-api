package com.artcom.y60.http;

import android.net.Uri;

public interface ResourceListener {

    public void onResourceAvailable(Uri pResourceUri);

    public void onResourceNotAvailable(Uri pResourceUri);
}
