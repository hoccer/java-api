package com.artcom.y60.data;

import android.content.ContentResolver;

public class GenericAndroidStreamableContent extends AndroidStreamableContent {

    public GenericAndroidStreamableContent(ContentResolver pContentResolver) {
        super(pContentResolver);
    }

    @Override
    public String getFilename() {
        return null;
    }

}
