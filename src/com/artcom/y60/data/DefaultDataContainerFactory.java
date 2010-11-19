package com.artcom.y60.data;

import java.io.FileNotFoundException;

public class DefaultDataContainerFactory extends DataContainerFactory {
    
    private static final String LOG_TAG = "DefaultDataContainerFactory";
    
    @Override
    public StreamableContent createStreamableContent(String pMimeType, String pFilename)
            throws FileNotFoundException {
        
        GenericStreamableContent streamableContent = new GenericStreamableContent();
        streamableContent.setContentType(pMimeType);
        streamableContent.setFilename(pFilename);
        
        return streamableContent;
    }
}
