/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GenericStreamableContent implements StreamableContent {

    String                              mFilename     = "filename.unkonwn";
    String                              mContentType;
    private final ByteArrayOutputStream mResultStream = new ByteArrayOutputStream();

    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(mResultStream.toByteArray());
    }

    @Override
    public long getStreamLength() throws IOException {
        return mResultStream.size();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return mResultStream;
    }

    @Override
    public String toString() {
        if (mContentType != null
                && (mContentType.contains("text") || mContentType.contains("json"))) {
            return mResultStream.toString();
        }

        return mFilename + " (" + mContentType + ")";
    }
}
