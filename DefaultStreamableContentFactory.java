package com.artcom.y60.data;

import java.io.FileNotFoundException;

public class DefaultStreamableContentFactory extends ContentFactory {

    private static final String LOG_TAG = "DefaultStreamableContentFactory";

    @Override
    public StreamableContent createStreamableContentContainerFrom(String pMimeType,
            long pStreamLength, String pFilename) throws FileNotFoundException {

        DynamicStreamableContent streamableContent = new DynamicStreamableContent();
        streamableContent.setContentType(pMimeType);

        return streamableContent;
    }
}
