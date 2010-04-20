package com.artcom.y60.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DynamicStreamableContent implements StreamableContent {

    String               mContentType;
    private OutputStream mResultStream = new ByteArrayOutputStream();

    public void setContentType(String pContentType) {
        mContentType = pContentType;
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
        // TODO Auto-generated method stub
        return 0;
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        mResultStream.write(buffer, offset, count);
    }

    public String toString() {
        return mResultStream.toString();
    }
}
