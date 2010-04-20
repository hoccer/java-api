package com.artcom.y60.data;

import java.io.InputStream;

public interface StreamableContent {

    public long getStreamLength();

    public InputStream getStream();

    public String getContentType();

}
