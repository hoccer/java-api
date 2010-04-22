package com.artcom.y60.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GenericStreamableContent implements StreamableContent {

    String                        mContentType;
    private ByteArrayOutputStream mResultStream = new ByteArrayOutputStream();

    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(mResultStream.toByteArray());
    }

    @Override
    public long getStreamLength() {
        return mResultStream.size();
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
