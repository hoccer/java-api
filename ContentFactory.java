package com.artcom.y60.data;

import java.io.FileNotFoundException;

public abstract class ContentFactory {

    public abstract StreamableContent createStreamableContentContainerFrom(String pMimeType,
            long pStreamLength, String pFilename) throws FileNotFoundException;

}
