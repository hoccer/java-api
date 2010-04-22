package com.artcom.y60.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamableString implements StreamableContent {

    byte[] mData;

    public StreamableString(String text) {
        mData = text.getBytes();
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(mData);
    }

    @Override
    public long getStreamLength() {
        return mData.length;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFilename() {
        return "data.txt";
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        // TODO Auto-generated method stub
    }
}
