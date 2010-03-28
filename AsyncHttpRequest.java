package com.artcom.y60.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.artcom.y60.Logger;

public class AsyncHttpRequest extends AsyncTask<String, Float, HttpResponse> {
    
    private static final String LOG_TAG      = "AsyncHttpConnection";
    
    public static final int     GET          = 0;
    public static final int     POST         = 1;
    public static final int     PUT          = 2;
    public static final int     DELETE       = 3;
    
    private static String       USER_AGENT   = "Y60/1.0 Android";
    
    private HttpEntity          mData;
    private int                 mType        = GET;
    
    private String              mContentType = "application/x-www-form-urlencoded";
    private final String        mAccept      = "text/html";
    
    private HttpResponseHandler mCallbackClass;
    
    private final OutputStream  resultStream = new ByteArrayOutputStream();
    
    private HttpRequestBase     mRequest;
    private HttpResponse        mResponse;
    private boolean             mHasFinished;
    
    public static void setUserAgent(String pAgent) {
        USER_AGENT = pAgent;
    }
    
    public void setData(String data) throws UnsupportedEncodingException {
        mData = new StringEntity(data);
    }
    
    public void setData(HttpEntity entity) {
        mData = entity;
    }
    
    public void setData(InputStream iStream, String pContentType) {
        mData = new InputStreamEntity(iStream, 1000);
        mContentType = pContentType;
    }
    
    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }
    
    public String getData() {
        return mData.toString();
    }
    
    public void setConnectionType(int type) {
        mType = type;
    }
    
    public int getConnectionType() {
        return mType;
    }
    
    public void registerResponseHandler(HttpResponseHandler callback) {
        mCallbackClass = callback;
    }
    
    @Override
    protected HttpResponse doInBackground(String... params) {
        try {
            return connect(params);
        } catch (IOException e) {
            Logger.e(LOG_TAG, e);
        }
        
        return null;
    }
    
    public boolean hasFinished() {
        return mHasFinished;
    }
    
    public HttpResponse connect(String... params) throws IOException {
        
        String url = params[0];
        Logger.v(LOG_TAG, "connecing to ", url);
        
        mResponse = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        mRequest = createRequest(params[0]);
        mResponse = httpClient.execute(mRequest);
        
        int status = mResponse.getStatusLine().getStatusCode();
        
        if (status == 200) {
            publishProgress(new Float(-1));
            
            if (resultStream == null) {
                return mResponse;
            }
            
            InputStream is = mResponse.getEntity().getContent();
            
            long downloaded = 0;
            long size = mResponse.getEntity().getContentLength();
            
            byte[] buffer = new byte[0xFFFF];
            int len;
            
            while ((len = is.read(buffer)) != -1) {
                publishProgress(new Float(downloaded / (float) size));
                
                resultStream.write(buffer, 0, len);
                downloaded += len;
            }
        }
        
        return mResponse;
    }
    
    private HttpRequestBase createRequest(String pUrl) {
        
        HttpRequestBase request = null;
        
        switch (getConnectionType()) {
            case POST:
                request = new HttpPost(pUrl);
                insertData((HttpPost) request);
                break;
            case PUT:
                request = new HttpPut(pUrl);
                insertData((HttpPut) request);
                break;
            default:
                request = new HttpGet(pUrl);
        }
        
        request.addHeader("User-Agent", USER_AGENT);
        
        logHeaders(request.getAllHeaders());
        
        return request;
    }
    
    private void insertData(HttpEntityEnclosingRequestBase post) {
        post.setEntity(mData);
        if (mContentType != null)
            post.addHeader("Content-Type", mContentType);
        post.addHeader("Accept", mAccept);
    }
    
    @Override
    protected void onProgressUpdate(Float... progress) {
        if (progress[0].floatValue() == -1) {
            mCallbackClass.onSuccess(mResponse);
        }
        
        mCallbackClass.onReceiving(progress[0]);
        
        super.onProgressUpdate(progress);
    }
    
    @Override
    protected void onPostExecute(HttpResponse pResponse) {
        mHasFinished = true;
        Logger.v(LOG_TAG, "on PostExecute");
        if (mCallbackClass == null) {
            return;
        }
        
        int status = pResponse.getStatusLine().getStatusCode();
        if (status >= 400 && status < 500) {
            onClientError(pResponse);
        } else if (status >= 500 && status < 600) {
            onServerError(pResponse);
        } else if (pResponse != null) {
            Logger.v(LOG_TAG, "in onPostExecute");
            mCallbackClass.onSuccess(pResponse);
        }
        
        super.onPostExecute(pResponse);
    }
    
    protected void onClientError(HttpResponse pResponse) {
        mCallbackClass.onError(pResponse);
    }
    
    protected void onServerError(HttpResponse pResponse) {
        mCallbackClass.onError(pResponse);
    }
    
    private void logHeaders(Header[] headers) {
        for (Header h : headers) {
            Logger.v(LOG_TAG, h);
        }
    }
    
    @Override
    public void onCancelled() {
        super.onCancelled();
        try {
            mRequest.abort();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
