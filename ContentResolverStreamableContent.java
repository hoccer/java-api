package com.artcom.y60.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;

public class ContentResolverStreamableContent extends DynamicStreamableContent {

    String               mContentType;
    private OutputStream mResultStream;
    private Uri          mContentResolverUri;

    public ContentResolverStreamableContent(OutputStream pOutputStream, Uri pContentResolverUri) {
        mResultStream = pOutputStream;
        mContentResolverUri = pContentResolverUri;
    }

    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }

    public Uri getContentResolverUri() {
        return mContentResolverUri;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public InputStream getStream() {
        return null;
    }

    @Override
    public long getStreamLength() {
        return 0;
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        mResultStream.write(buffer, offset, count);
    }

    public String toString() {
        return mResultStream.toString();
    }

    @Override
    public String getFilename() {
        return "data";
    }
}
