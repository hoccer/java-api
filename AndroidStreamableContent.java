package com.artcom.y60.data;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.net.Uri;

import com.artcom.y60.Logger;

public abstract class AndroidStreamableContent implements StreamableContent {

    private static final String LOG_TAG              = "AndroidStreamableContent";
    protected ContentResolver   mContentResolver;
    private Uri                 mContentResolverUri;
    protected String            mContentType;
    OutputStream                mAndroidOutputStream = null;

    public AndroidStreamableContent(ContentResolver pContentResolver) {
        mContentResolver = pContentResolver;
    }

    public Uri getContentResolverUri() {
        return mContentResolverUri;
    }

    protected void setContentResolverUri(Uri dataLocation) throws FileNotFoundException {
        Logger.v(LOG_TAG, "setContentResolverUri to: ", dataLocation,
                " and create outputstream from content resolver");
        mContentResolverUri = dataLocation;
        mAndroidOutputStream = mContentResolver.openOutputStream(dataLocation);
    }

    public InputStream getContentResolverInputStream() throws FileNotFoundException {
        BufferedInputStream stream = new BufferedInputStream(mContentResolver
                .openInputStream(getContentResolverUri()));
        return stream;

    }

    public OutputStream getOutputStream() {
        return mAndroidOutputStream;
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        getOutputStream().write(buffer, offset, count);
    }
}
