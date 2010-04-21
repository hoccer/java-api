package com.artcom.y60.data;

import java.io.FileNotFoundException;

public abstract class DataContainerFactory {

    public abstract StreamableContent createStreamableContent(String pMimeType,
            long pStreamLength, String pFilename) throws FileNotFoundException;

}
