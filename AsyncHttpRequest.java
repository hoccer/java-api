package com.artcom.y60.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.artcom.y60.Logger;
import com.artcom.y60.thread.ThreadedTask;

public abstract class AsyncHttpRequest extends ThreadedTask {
    
    private static final String   LOG_TAG       = "AsyncHttpConnection";
    
    private static String         USER_AGENT    = "Y60/1.0 Android";
    
    private final HttpRequestBase mRequest;
    
    private final OutputStream    mResultStream = new ByteArrayOutputStream();
    
    private HttpResponse          mResponse     = null;
    
    public AsyncHttpRequest(String pUrl) {
        mRequest = createRequest(pUrl);
        mRequest.addHeader("User-Agent", USER_AGENT);
    }
    
    public static void setUserAgent(String pAgent) {
        USER_AGENT = pAgent;
    }
    
    protected HttpRequestBase getRequest() {
        return mRequest;
    }
    
    @Override
    public void doInBackground() {
        try {
            connect();
        } catch (IOException e) {
            Logger.e(LOG_TAG, e);
        }
    }
    
    public HttpResponse connect(String... params) throws IOException {
        
        setProgress(1);
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        mResponse = httpClient.execute(mRequest);
        setProgress(2);
        
        int status = mResponse.getStatusLine().getStatusCode();
        Logger.v(LOG_TAG, "response status code is ", status);
        
        if (status == 200) {
            
            InputStream is = mResponse.getEntity().getContent();
            long downloaded = 0;
            long size = mResponse.getEntity().getContentLength();
            byte[] buffer = new byte[0xFFFF];
            int len;
            while ((len = is.read(buffer)) != -1) {
                setProgress((int) (downloaded / size));
                mResultStream.write(buffer, 0, len);
                downloaded += len;
            }
        }
        
        return mResponse;
    }
    
    abstract protected HttpRequestBase createRequest(String pUrl);
    
    @Override
    protected void onPostExecute() {
        Logger.v(LOG_TAG, "on PostExecute");
        super.onPostExecute();
        
        if (mResponse == null) {
            return;
        }
        
        int status = mResponse.getStatusLine().getStatusCode();
        if (status >= 400 && status < 500) {
            onClientError();
        } else if (status >= 500 && status < 600) {
            onServerError();
        } else {
            onSuccess();
        }
    }
    
    protected void onSuccess() {
    }
    
    protected void onClientError() {
    }
    
    protected void onServerError() {
    }
}
