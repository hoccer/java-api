package com.artcom.y60.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GenericStreamableContent implements StreamableContent {
    
    String                              mFilename     = "filename.unkonwn";
    String                              mContentType;
    private final ByteArrayOutputStream mResultStream = new ByteArrayOutputStream();
    
    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }
    
    @Override
    public String getContentType() {
        return mContentType;
    }
    
    public String getFilename() {
        return mFilename;
    }
    
    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }
    
    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(mResultStream.toByteArray());
    }
    
    @Override
    public long getStreamLength() {
        return mResultStream.size();
    }
    
    @Override
    public String toString() {
        return mResultStream.toString();
    }
    
    @Override
    public OutputStream openOutputStream() {
        return mResultStream;
    }
}
