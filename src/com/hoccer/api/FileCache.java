package com.hoccer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.hoccer.data.StreamableContent;
import com.hoccer.http.MultipartHttpEntity;
import com.hoccer.thread.StatusHandler;

public class FileCache extends CloudService {

    private long    downloadTime;
    private boolean isFetchStopped;
    private int     progress;

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

        getClientConfig();
        String url = ClientConfig.getFileCacheBaseUri() + "/?expires_in=" + secondsUntilExipred;
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
            throw new HttpResponseException(statuscode, "Unexpected status code " + statuscode
                    + " while requesting " + locationUri);
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
