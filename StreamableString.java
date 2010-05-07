package com.artcom.y60.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamableString implements StreamableContent {

    protected ByteArrayOutputStream mData = new ByteArrayOutputStream();

    public StreamableString(String text) throws IOException {
        mData.write(text.getBytes());
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(mData.toByteArray());
    }

    @Override
    public OutputStream openOutputStream() {
        return mData;
    }

    @Override
    public long getStreamLength() {
        return mData.size();
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFilename() {
        return "data.txt";
    }

}
