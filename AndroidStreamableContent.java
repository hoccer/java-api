package com.artcom.y60.data;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.net.Uri;

public abstract class AndroidStreamableContent implements StreamableContent {
    
    ContentResolver mContentResolver;
    private Uri     mContentResolverUri;
    String          mContentType;
    
    public AndroidStreamableContent(ContentResolver pContentResolver) {
        mContentResolver = pContentResolver;
    }
    
    public Uri getContentResolverUri() {
        return mContentResolverUri;
    }
    
    protected void setCotentResolverUri(Uri dataLocation) {
        mContentResolverUri = dataLocation;
    }
    
    public OutputStream openOutputStream() throws FileNotFoundException {
        return mContentResolver.openOutputStream(getContentResolverUri());
    }
    
    public String getContentType() {
        return mContentResolver.getType(getContentResolverUri());
    }
    
    @Override
    public long getStreamLength() throws FileNotFoundException {
        return mContentResolver.openAssetFileDescriptor(getContentResolverUri(), "r").getLength();
    }
    
    public InputStream openInputStream() throws FileNotFoundException {
        return mContentResolver.openInputStream(getContentResolverUri());
    }
}
