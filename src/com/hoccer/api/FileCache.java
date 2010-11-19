package com.hoccer.api;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;

import com.hoccer.data.*;
import com.hoccer.http.*;

public class FileCache extends CloudService {

    public FileCache(ClientDescription config) {
        super(config);
    }

    public String store(StreamableContent data, int secondsToBeAvailable)
            throws ClientProtocolException, IOException {

        MultipartHttpEntity multipart = new MultipartHttpEntity();
        multipart.addPart(data.getFilename(), data);

        HttpPost request = new HttpPost("http://filecache.sandbox.hoccer.com/");
        request.setEntity(multipart);
        HttpResponse response = getHttpClient().execute(request);

        return convertResponseToString(response);
    }
}
