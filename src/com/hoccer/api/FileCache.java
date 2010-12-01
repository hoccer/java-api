package com.hoccer.api;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;

import com.hoccer.data.*;
import com.hoccer.http.*;
import com.hoccer.thread.*;

public class FileCache extends CloudService {

    private long         downloadTime;
    private boolean      isFetchStopped;
    private int          progress;
    private final String baseUri = "http://filecache.sandbox.hoccer.com";

    public FileCache(ClientConfig config) {
        super(config);
    }

    public String store(StreamableContent data, int secondsUntilExipred)
            throws ClientProtocolException, IOException {

        MultipartHttpEntity multipart = new MultipartHttpEntity();
        multipart.addPart("upload", data);
        multipart.registerStatusHandler(new StatusHandler() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onProgress(int progress) {
                FileCache.this.progress = progress;
            }

            @Override
            public void onError(Throwable e) {
            }
        });

        String url = baseUri + "/?expires_in=" + secondsUntilExipred;
        HttpPost request = new HttpPost(sign(url));
        request.setEntity(multipart);
        HttpResponse response = getHttpClient().execute(request);

        return convertResponseToString(response);
    }

    public void fetch(String locationUri, StreamableContent data) throws ClientProtocolException,
            IOException {
        isFetchStopped = false;

        HttpGet request = new HttpGet(locationUri);
        HttpResponse response = getHttpClient().execute(request);

        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode != 200) {
            throw new HttpResponseException(statuscode, "Unexpected status code while requesting"
                    + locationUri);
        }

        InputStream is = response.getEntity().getContent();
        OutputStream storageStream = data.openOutputStream();
        long downloaded = 0;
        long size = response.getEntity().getContentLength();
        byte[] buffer = new byte[0xFFFF];
        int len;
        long downloadStart = System.currentTimeMillis();
        while ((len = is.read(buffer)) != -1) {
            if (isFetchStopped) {
                return;
            }

            setProgress((int) ((downloaded / (double) size) * 100));
            storageStream.write(buffer, 0, len);
            downloaded += len;
        }
        downloadTime = System.currentTimeMillis() - downloadStart;
    }

    private void setProgress(int percent) {
        this.progress = percent;
    }

    public int getProgress() {
        return progress;
    }

    public void stopFetch() {
        isFetchStopped = true;
    }
}
