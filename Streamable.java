package com.artcom.y60.data;

import java.io.InputStream;

public interface Streamable {
    
    public long getStreamLength();
    
    public InputStream getStream();
}
