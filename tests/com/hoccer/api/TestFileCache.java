/*
 *  Copyright (C) 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 *  These coded instructions, statements, and computer programs contain
 *  proprietary information of Hoccer GmbH Berlin, and are copy protected
 *  by law. They may be used, modified and redistributed under the terms
 *  of GNU General Public License referenced below. 
 *     
 *  Alternative licensing without the obligations of the GPL is
 *  available upon request.
 * 
 *  GPL v3 Licensing:
 * 
 *  This file is part of the "Hoccer Java-API".
 * 
 *  Hoccer Java-API is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Hoccer Java-API is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Hoccer Java-API. If not, see <http: * www.gnu.org/licenses/>.
 */
package com.hoccer.api;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import org.junit.*;

import com.hoccer.data.*;

public class TestFileCache {

    @Test
    public void storeTextInFileCache() throws Exception {
        FileCache filecache = new FileCache(new ClientDescription("File Cache Unit Test"));

        String locationUri = filecache.store(new StreamableString("hello world"), 1);
        assertThat(locationUri, containsString("http://filecache.sandbox.hoccer.com/"));

        StreamableContent data = new StreamableString();
        filecache.fetch(locationUri, data);

        assertThat(data.toString(), is(equalTo("hello world")));

        Thread.sleep(2000);
        filecache.fetch(locationUri, data);
    }

    @Test
    public void storeBinaryDataInFileCache() throws Exception {
        FileCache filecache = new FileCache(new ClientDescription("File Cache Unit Test"));

        GenericStreamableContent content = new GenericStreamableContent();
        content.setFilename("data.png");
        content.setContentType("image/png");
        byte[] data = { 23, 42, 23 };
        content.openOutputStream().write(data);

        String locationUri = filecache.store(content, 2);
        assertThat(locationUri, containsString("http://filecache.sandbox.hoccer.com/"));

    }
}
