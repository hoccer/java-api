package com.artcom.y60.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.net.Uri;

import com.artcom.y60.Logger;

public abstract class AndroidStreamableContent implements StreamableContent {

    private static final String LOG_TAG = "AndroidStreamableContent";
    ContentResolver             mContentResolver;
    private Uri                 mContentResolverUri;
    protected String            mContentType;

    public AndroidStreamableContent(ContentResolver pContentResolver) {
        mContentResolver = pContentResolver;
    }

    public Uri getContentResolverUri() {
        return mContentResolverUri;
    }

    protected void setContentResolverUri(Uri dataLocation) throws BadContentResolverUriException {
        if (dataLocation == null) {
            throw new BadContentResolverUriException();
        }

        mContentResolverUri = dataLocation;
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public OutputStream openOutputStream() throws IOException {
        return mContentResolver.openOutputStream(getContentResolverUri());
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public InputStream openInputStream() throws IOException {
        return mContentResolver.openInputStream(getContentResolverUri());
    }

    @Override
    public String getContentType() {

        if (getContentResolverUri() != null) {
            String contentType = mContentResolver.getType(getContentResolverUri());
            if (contentType != null) {
                return contentType;
            }
        }

        return mContentType;
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public long getStreamLength() throws IOException {
        if (mContentResolverUri == null) {
            Logger.e(LOG_TAG, "no valid content resolver uri!");
            // throw new BadContentResolverUriException();
        }

        return mContentResolver.openAssetFileDescriptor(getContentResolverUri(), "r").getLength();
    }
}
