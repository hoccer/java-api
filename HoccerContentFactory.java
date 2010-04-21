package com.artcom.y60.data;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

public class HoccerContentFactory extends ContentFactory {

    private static final String LOG_TAG = "HoccerContentFactory";
    private Context             mContext;

    public HoccerContentFactory(Context pContext) {
        mContext = pContext;
    }

    @Override
    public StreamableContent createStreamableContentContainerFrom(String pMimeType,
            long pStreamLength, String pFilename) throws FileNotFoundException {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, pMimeType);
        values.put(MediaStore.Images.Media.SIZE, pStreamLength);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, pFilename);

        Uri imageUri = mContext.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outputStream = mContext.getContentResolver().openOutputStream(imageUri);

        ContentResolverStreamableContent streamableContent = new ContentResolverStreamableContent(
                outputStream, imageUri);
        streamableContent.setContentType(pMimeType);
        return streamableContent;
    }
}
