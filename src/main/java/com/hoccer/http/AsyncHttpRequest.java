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
package com.hoccer.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Logger;

import com.hoccer.util.HoccerLoggers;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.hoccer.data.GenericStreamableContent;
import com.hoccer.data.StreamableContent;
import com.hoccer.thread.ThreadedTask;

public abstract class AsyncHttpRequest extends ThreadedTask {

    private static final Logger LOG = HoccerLoggers.getLogger(AsyncHttpRequest.class);

    private static final String   LOG_TAG                  = "AsyncHttpRequest";

    private DefaultHttpClient     mHttpClient;
    private final HttpRequestBase mRequest;

    private HttpResponse          mResponse                = null;
    private StreamableContent     mResponseContent         = null;

    private HttpResponseHandler   mResponseHandlerCallback = null;

    private boolean               mIsRequestCompleted      = false;
    private long                  mUploadTime              = 0;
    private long                  mDownloadTime            = 0;

    // special feature for retrying downloads
    private int                   mMaxRetries              = 1;

    public AsyncHttpRequest(String pUrl) {
        mRequest = createRequest(pUrl);

        HttpParams httpParams = new BasicHttpParams();
        setHttpClient(new HttpClientWithKeystore(httpParams));
    }

    public AsyncHttpRequest(String pUrl, DefaultHttpClient pHttpClient) {
        mRequest = createRequest(pUrl);
        setHttpClient(pHttpClient);
    }

    public void setMaxRetries(int maxRetries) {
        mMaxRetries = maxRetries;
    }

    // Only used internally to reuse code for both constructors
    private void setHttpClient(DefaultHttpClient pHttpClient) {
        mHttpClient = pHttpClient;

        // overwrite user-agent if it's not already customized
        Object userAgent = mHttpClient.getParams().getParameter("http.useragent");
        if (userAgent == null || userAgent.toString().contains("Apache-HttpClient")) {
            mHttpClient.getParams().setParameter("http.useragent", "Hoccer Java API");
        }

        // remember redirects
        mHttpClient.setRedirectHandler(new DefaultRedirectHandler() {
            @Override
            public URI getLocationURI(HttpResponse response, HttpContext context)
                    throws ProtocolException {
                URI uri = super.getLocationURI(response, context);
                mRequest.setURI(uri);
                return uri;
            }
        });
    }

    public String getBodyAsString() {
        if (mResponseContent == null) {
            return "";
        }
        return mResponseContent.toString();
    }

    public StreamableContent getBodyAsStreamableContent() {
        return mResponseContent;
    }

    public void setStreamableContent(StreamableContent pStreamableContent) {
        mResponseContent = pStreamableContent;
    }

    public void setAcceptedMimeType(String pMimeType) {
        getRequest().addHeader("Accept", pMimeType);
    }

    public boolean isConnecting() {
        return getProgress() == 1;
    }

    public boolean isRequestCompleted() {
        return mIsRequestCompleted;
    }

    /**
     * @return true: server responded with 2xx, payload not neccessarily downloaded
     */
    public boolean wasSuccessful() {
        int status = getStatusCode();
        return status >= 200 && status < 300;
    }

    public boolean hadClientError() {
        int status = getStatusCode();
        return status >= 400 && status < 500;
    }

    public boolean hadServerError() {
        int status = getStatusCode();
        return status >= 500 && status < 600;
    }

    @Override
    public void interrupt() {
        if (!mRequest.isAborted()) {
            mRequest.abort();
        }
        super.interrupt();
    }

    @Override
    public void doInBackground() {

        setUploadProgress(1);

        int retryCount = 0;
        while(retryCount < mMaxRetries) {
            if(mMaxRetries > 1) {
                LOG.info("retry " + retryCount + "/" + mMaxRetries);
            }
            try {
                long uploadStart = System.currentTimeMillis();
                mResponse = mHttpClient.execute(mRequest);
                mUploadTime = System.currentTimeMillis() - uploadStart;
            } catch (ClientProtocolException e) {
                onClientError(e);
                return;
            } catch (SocketException e) {
                onClientError(e);
                return;
            } catch (SecurityException e) {
                onClientError(e);
                return;
            } catch (IOException e) {
                onIoError(e);
                return;
            }

            if (mResponse == null) {
                onClientError(new NullPointerException("expected http response object is null"));
                return;
            }

            int sc = mResponse.getStatusLine().getStatusCode();
            if(mMaxRetries > 1) {
                LOG.info("retry status " + sc);
            }
            if(sc == 404 || sc == 500) {
                retryCount++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    onClientError(e);
                    return;
                }
            } else {
                break;
            }
        }

        onHttpHeaderAvailable(mResponse.getAllHeaders());

        try {
            InputStream is = mResponse.getEntity().getContent();
            OutputStream storageStream = mResponseContent.openNewOutputStream();
            long downloaded = 0;
            long size = mResponse.getEntity().getContentLength();
            byte[] buffer = new byte[0xFFFF];
            int len;
            long downloadStart = System.currentTimeMillis();
            while ((len = is.read(buffer)) != -1) {
                if (isInterrupted()) {
                    onClientError(new InterruptedException("download is interruped"));
                    return;
                }
                setDownloadProgress((int) ((downloaded / (double) size) * 100));
                storageStream.write(buffer, 0, len);
                downloaded += len;
            }
            setDownloadProgress(100);
            mDownloadTime = System.currentTimeMillis() - downloadStart;
        } catch (IOException e) {
            onIoError(e);
            return;
        } catch (Exception e) {
            onClientError(e);
            return;
        }
        mIsRequestCompleted = true;
    }

    public int getStatusCode() {
        if (mResponse != null) {
            return mResponse.getStatusLine().getStatusCode();
        } else {
            return -1;
        }
    }

    public void registerResponseHandler(HttpResponseHandler responseHandler) {
        mResponseHandlerCallback = responseHandler;
    }

    /**
     * @return uri of the request (gets updated when redirected)
     */
    public String getUri() {
        return mRequest.getURI().toString();
    }

    public String getHeader(String pHeaderName) {
        return mResponse.getFirstHeader(pHeaderName).getValue();
    }

    abstract protected HttpRequestBase createRequest(String pUrl);

    protected HttpRequestBase getRequest() {
        return mRequest;
    }

    public HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        Header[] headersArray = mRequest.getAllHeaders();
        for (int i = 0; i < headersArray.length; i++) {
            headers.put(headersArray[i].getName(), headersArray[i].getValue());
        }
        return headers;
    }

    @Override
    protected void onPostExecute() {

        if (mResponse == null) {
            return;
        }

        int status = getStatusCode();
        if (hadClientError()) {
            onClientError(status);
        } else if (hadServerError()) {
            onServerError(status);
        } else if (wasSuccessful()) {
            onSuccess(status);
        } else {
            onClientError(new Exception("do not know what to do with status code "
                    + getStatusCode()));
        }

        mIsRequestCompleted = true;

        super.onPostExecute();
    }

    protected void onIoError(IOException e) {
        mIsRequestCompleted = true;
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onError(e);
        }
    }

    protected void onClientError(Exception e) {
        mIsRequestCompleted = true;
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onError(e);
        }
    }

    protected void onHttpHeaderAvailable(Header[] pHeaders) {

        HashMap<String, String> headers = new HashMap<String, String>();
        for (int i = 0; i < pHeaders.length; i++) {
            headers.put(pHeaders[i].getName(), pHeaders[i].getValue());
        }

        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onHeaderAvailable(headers);
        }

        if (mResponseContent == null) {
            GenericStreamableContent contentReceiver = new GenericStreamableContent();
            mResponseContent = contentReceiver;
        }

        if (mResponseContent instanceof GenericStreamableContent) {
            setTypeAndFilename((GenericStreamableContent) mResponseContent, headers, getUri());
        }
    }

    public static void setTypeAndFilename(GenericStreamableContent contentReceiver,
            HashMap<String, String> headers, String uri) {
        String type = headers.get("Content-Type");
        if (type == null) {
            type = "text/plain";
        }

        contentReceiver.setContentType(type);

        String filename = headers.get("Content-Disposition");
        if (filename == null || !filename.matches(".*filename=\".*\"")) {
            filename = uri.substring(uri.lastIndexOf('/'));
        } else {
            filename = filename.substring(filename.indexOf("filename=\"") + 10,
                    filename.length() - 1);
        }
        contentReceiver.setFilename(filename);
    }

    protected void onSuccess(int pStatusCode) {
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onSuccess(pStatusCode, mResponseContent);
        }
    }

    protected void onClientError(int pStatusCode) {
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onError(pStatusCode, mResponseContent);
        }
    }

    protected void onServerError(int pStatusCode) {
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onError(pStatusCode, mResponseContent);
        }
    }

    protected void setDownloadProgress(int pProgress) {
        super.setProgress(pProgress);
        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onReceiving(pProgress);
        }
    }

    protected void setUploadProgress(int pProgress) {
        super.setProgress(pProgress);

        if (mResponseHandlerCallback != null) {
            mResponseHandlerCallback.onSending(pProgress);
        }
    }

    public void removeResponseHandler() {
        mResponseHandlerCallback = null;
    }

    public long getUploadTime() {
        return mUploadTime;
    }

    public long getDownloadTime() {
        return mDownloadTime;
    }

    public void addAdditionalHeaderParam(String key, String value) {
        mRequest.addHeader(key, value);
        // mHttpClient.se getParams().setParameter(key, value);
    }

    @Override
    public boolean isTaskCompleted() {
        synchronized (this) {
            return isRequestCompleted();
        }
    }
}
