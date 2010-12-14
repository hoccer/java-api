package com.hoccer.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hoccer.data.MonitoredInputStream;
import com.hoccer.data.StreamableContent;
import com.hoccer.thread.StatusHandler;

public abstract class AsyncHttpRequestWithBody extends AsyncHttpRequest {

    private static final String LOG_TAG = "AsyncHttpRequestWithBody";

    public AsyncHttpRequestWithBody(String pUrl) {
        super(pUrl);
    }

    public AsyncHttpRequestWithBody(String pUrl, DefaultHttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }

    public void setBody(String pStringData) {
        insert(pStringData, "text/plain", getRequest());
    }

    public void setBody(StreamableContent pStreamableData) throws IOException {

        final long streamLength = pStreamableData.getStreamLength();

        InputStreamEntity entity = new InputStreamEntity(new MonitoredInputStream(pStreamableData
                .openInputStream()) {

            @Override
            public void onBytesRead(long totalNumBytesRead) {

                double progress = (totalNumBytesRead / (double) streamLength) * 100;
                setUploadProgress((int) progress);
            }

        }, pStreamableData.getStreamLength());

        getRequest().addHeader("Content-Disposition",
                " attachment; filename=\"" + pStreamableData.getFilename() + "\"");
        entity.setContentType(pStreamableData.getContentType());
        getRequest().setEntity(entity);
        getRequest().addHeader("Content-Type", pStreamableData.getContentType());
    }

    public void setBody(HttpEntity pEntity) {
        getRequest().setEntity(pEntity);
    }

    public void setBody(MultipartHttpEntity pMultipart) {
        pMultipart.registerStatusHandler(new StatusHandler() {

            @Override
            public void onSuccess() {
                setUploadProgress(100);
            }

            @Override
            public void onProgress(int progress) {
                setUploadProgress(Math.min(Math.max(0, progress), 100));
            }

            @Override
            public void onError(Throwable e) {
                // TODO Auto-generated method stub

            }
        });
        getRequest().setEntity(pMultipart);

    }

    public void setBody(Map<String, String> params) {
        insert(urlEncodeValues(params), "application/x-www-form-urlencoded", getRequest());
    }

    public static String urlEncodeValues(Map<String, String> pData) {

        StringBuffer tmp = new StringBuffer();
        Set keys = pData.keySet();
        int idx = 0;
        for (Object key : keys) {
            tmp.append(String.valueOf(key));
            tmp.append("=");
            tmp.append(URLEncoder.encode(String.valueOf(pData.get(key))));

            idx += 1;
            if (idx < keys.size()) {
                tmp.append("&");
            }
        }

        return tmp.toString();
    }

    static void insert(String pBody, String pContentType, String pAcceptMimeType,
            HttpEntityEnclosingRequestBase pMethod) {

        insert(pBody, pContentType, pMethod);
        pMethod.addHeader("Accept", pAcceptMimeType);
    }

    static void insert(String pBody, String pContentType, HttpEntityEnclosingRequestBase pMethod) {

        StringEntity entity;
        try {
            entity = new StringEntity(pBody);
            pMethod.setEntity(entity);
            pMethod.addHeader("Content-Type", pContentType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected HttpEntityEnclosingRequestBase getRequest() {
        return (HttpEntityEnclosingRequestBase) super.getRequest();
    }
}
