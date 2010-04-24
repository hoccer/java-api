package com.artcom.y60.data;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamableContent {
    
    public long getStreamLength();
    
    public InputStream openInputStream();
    
    public String getContentType();
    
    public String getFilename();
    
    public OutputStream openOutputStream() throws FileNotFoundException;
}
