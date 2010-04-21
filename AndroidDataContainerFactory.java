package com.artcom.y60.data;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

public class AndroidDataContainerFactory extends DataContainerFactory {

    private static final String LOG_TAG = "HoccerContentFactory";
    private ContentResolver     mContentResolver;

    public AndroidDataContainerFactory(ContentResolver pContentResolver) {
        mContentResolver = pContentResolver;
    }

    @Override
    public StreamableContent createStreamableContent(String ContentType, long pStreamLength,
            String pFilename) throws FileNotFoundException {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, ContentType);
        values.put(MediaStore.Images.Media.SIZE, pStreamLength);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, pFilename);

        Uri imageUri = mContentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outputStream = mContentResolver.openOutputStream(imageUri);

        AndroidStreamableContent streamableContent = new AndroidStreamableContent(outputStream,
                imageUri, ContentType);

        return streamableContent;
    }
}
