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
    private Uri                 mDataUri;
    protected String            mContentType;

    public AndroidStreamableContent(ContentResolver pContentResolver) {
        mContentResolver = pContentResolver;
    }

    public Uri getDataUri() {
        return mDataUri;
    }

    protected void setDataUri(Uri dataLocation) throws BadContentResolverUriException {
        if (dataLocation == null) {
            throw new BadContentResolverUriException();
        }

        mDataUri = dataLocation;
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public OutputStream openOutputStream() throws IOException {
        return mContentResolver.openOutputStream(getDataUri());
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public InputStream openInputStream() throws IOException {
        return mContentResolver.openInputStream(getDataUri());
    }

    @Override
    public String getContentType() {

        if (getDataUri() != null) {
            String contentType = mContentResolver.getType(getDataUri());
            if (contentType != null) {
                return contentType;
            }
        }

        return mContentType;
    }

    // override this in subclass, if you dont set a contentresolver uri
    @Override
    public long getStreamLength() throws IOException {
        if (mDataUri == null) {
            Logger.e(LOG_TAG, "no valid content resolver uri!");
            // throw new BadContentResolverUriException();
        }

        return mContentResolver.openAssetFileDescriptor(getDataUri(), "r").getLength();
    }

    protected boolean isFileSchemeUri() {
        return "file".equals(mDataUri.getScheme());
    }
}
