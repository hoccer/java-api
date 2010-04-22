package com.artcom.y60.data;

import java.io.IOException;
import java.io.InputStream;

public interface StreamableContent {

    public long getStreamLength();

    public InputStream getInputStream();

    public String getContentType();

    public String getFilename();

    public void write(byte[] buffer, int offset, int count) throws IOException;

}
