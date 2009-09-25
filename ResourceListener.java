package com.artcom.y60.http;

import android.net.Uri;

public interface ResourceListener {

    @Deprecated
    public void onResourceChanged(Uri pResourceUri);

    public void onResourceAvailable(Uri pResourceUri);

    public void onResourceNotAvailable(Uri pResourceUri);
}
