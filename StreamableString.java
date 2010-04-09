package com.artcom.y60.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StreamableString implements Streamable {
    
    byte[] mData;
    
    public StreamableString(String text) {
        mData = text.getBytes();
    }
    
    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(mData);
    }
    
    @Override
    public long getStreamLength() {
        return mData.length;
    }
    
}
