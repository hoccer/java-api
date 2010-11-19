package com.hoccer.data;

import java.io.*;

public class StreamableString implements StreamableContent {

    protected ByteArrayOutputStream mData = new ByteArrayOutputStream();

    public StreamableString(String text) throws IOException {
        mData.write(text.getBytes());
    }

    public StreamableString() throws IOException {

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

    @Override
    public String toString() {
        return mData.toString();
    }

}
