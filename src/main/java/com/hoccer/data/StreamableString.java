/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com> These coded instructions,
 * statements, and computer programs contain proprietary information of Hoccer GmbH Berlin, and are
 * copy protected by law. They may be used, modified and redistributed under the terms of GNU
 * General Public License referenced below. Alternative licensing without the obligations of the GPL
 * is available upon request. GPL v3 Licensing: This file is part of the "Linccer Java-API". Linccer
 * Java-API is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Linccer Java-API is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with Linccer Java-API. If
 * not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamableString implements StreamableContent {

    protected ByteArrayOutputStream mData = new ByteArrayOutputStream();

    public StreamableString(String text) throws IOException {
        mData.write(text.getBytes());
    }

    public StreamableString() throws IOException {

    }

    @Override
    public InputStream openNewInputStream() {
        return openRawInputStream();
    }

    @Override
    public OutputStream openNewOutputStream() {
        return openRawOutputStream();
    }

    @Override
    public InputStream openRawInputStream() {
        return new ByteArrayInputStream(mData.toByteArray());
    }

    @Override
    public OutputStream openRawOutputStream() {
        return mData;
    }

    @Override
    public long getNewStreamLength() {
        return mData.size();
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFilename() {
        return "data.txt";
    }

    @Override
    public String toString() {
        return mData.toString();
    }

}
