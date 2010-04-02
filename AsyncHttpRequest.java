package com.artcom.y60.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.artcom.y60.HttpHelper;
import com.artcom.y60.Logger;
import com.artcom.y60.thread.ThreadedTask;

public abstract class AsyncHttpRequest extends ThreadedTask {
    
    private static final String LOG_TAG       = "AsyncHttpConnection";
    
    private static String       USER_AGENT    = "Y60/1.0 Android";
    
    private HttpEntity          mData;
    
    private String              mContentType  = "application/x-www-form-urlencoded";
    private final String        mAccept       = "text/html";
    
    private final OutputStream  mResultStream = new ByteArrayOutputStream();
    
    private final String        mUrl;
    private HttpResponse        mResponse     = null;
    
    public AsyncHttpRequest(String pUrl) {
        mUrl = pUrl;
    }
    
    public static void setUserAgent(String pAgent) {
        USER_AGENT = pAgent;
    }
    
    private void setData(String data) throws UnsupportedEncodingException {
        mData = new StringEntity(data);
    }
    
    private void setData(HttpEntity entity) {
        mData = entity;
    }
    
    private void setData(InputStream iStream, String pContentType) {
        mData = new InputStreamEntity(iStream, 1000);
        mContentType = pContentType;
    }
    
    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }
    
    public String getData() {
        return mData.toString();
    }
    
    @Override
    public void doInBackground() {
        try {
            connect(mUrl);
        } catch (IOException e) {
            Logger.e(LOG_TAG, e);
        }
    }
    
    public HttpResponse connect(String... params) throws IOException {
        
        String url = params[0];
        Logger.v(LOG_TAG, "connecing to ", url);
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest mRequest = createRequest(params[0]);
        mRequest.addHeader("User-Agent", USER_AGENT);
        mResponse = httpClient.execute(mRequest);
        
        int status = mResponse.getStatusLine().getStatusCode();
        Logger.v(LOG_TAG, "response is ", HttpHelper.extractBodyAsString(mResponse.getEntity()));
        
        if (status == 200) {
            setProgress(1);
            
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
    
    protected void insertData(HttpEntityEnclosingRequestBase post) {
        post.setEntity(mData);
        if (mContentType != null)
            post.addHeader("Content-Type", mContentType);
        post.addHeader("Accept", mAccept);
    }
    
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
