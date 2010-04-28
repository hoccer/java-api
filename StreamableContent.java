package com.artcom.y60.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamableContent {

    public long getStreamLength() throws IOException;

    public InputStream openInputStream() throws IOException;

    public String getContentType();

    public String getFilename();

    public OutputStream openOutputStream() throws IOException;
}
