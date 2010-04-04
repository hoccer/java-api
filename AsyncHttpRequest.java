package com.artcom.y60.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
    
    public String getBodyAsString() {
        return mResultStream.toString();
    }
    
    public boolean isConnecting() {
        return getProgress() == 1;
    }
    
    @Override
    public void doInBackground() {
        
        setProgress(1);
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            mResponse = httpClient.execute(mRequest);
        } catch (ClientProtocolException e) {
            onClientError(e);
            return;
        } catch (SocketException e) {
            onClientError(e);
            return;
        } catch (IOException e) {
            onIoError(e);
            return;
        }
        setProgress(2);
        
        int status = mResponse.getStatusLine().getStatusCode();
        Logger.v(LOG_TAG, "response status code is ", status);
        
        if (status >= 200 && status < 300) {
            
            try {
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
            } catch (IOException e) {
                onIoError(e);
                return;
            }
        }
        
    }
    
    abstract protected HttpRequestBase createRequest(String pUrl);
    
    protected HttpRequestBase getRequest() {
        return mRequest;
    }
    
    @Override
    protected void onPostExecute() {
        
        if (mResponse == null) {
            onClientError(new NullPointerException("response of request " + mRequest.getURI()
                    + " was null"));
            return;
        }
        
        int status = mResponse.getStatusLine().getStatusCode();
        if (status >= 400 && status < 500) {
            onClientError(status);
        } else if (status >= 500 && status < 600) {
            onServerError(status);
        } else {
            onSuccess(status);
        }
        
        super.onPostExecute();
    }
    
    protected void onIoError(IOException e) {
        Logger.e(LOG_TAG, e);
    }
    
    protected void onClientError(Exception e) {
        Logger.e(LOG_TAG, e);
    }
    
    protected void onSuccess(int pStatusCode) {
    }
    
    protected void onClientError(int pStatusCode) {
        Logger.e(LOG_TAG, "Error response code was ", pStatusCode);
    }
    
    protected void onServerError(int pStatusCode) {
        Logger.e(LOG_TAG, "Error response code was ", pStatusCode);
    }
}
