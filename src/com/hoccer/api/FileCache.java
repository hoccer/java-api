package com.hoccer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.hoccer.data.StreamableContent;
import com.hoccer.http.AsyncHttpPut;
import com.hoccer.http.AsyncHttpRequest;
import com.hoccer.http.HttpResponseHandler;
import com.hoccer.http.MultipartHttpEntity;

public class FileCache extends CloudService {

    private long                                    downloadTime;
    private boolean                                 isFetchStopped;
    private int                                     progress;

    private final HashMap<String, AsyncHttpRequest> mOngoingRequests = new HashMap<String, AsyncHttpRequest>();

    public FileCache(ClientConfig config) {
        super(config);
    }

    public HashMap<String, AsyncHttpRequest> getOngoingRequests() {
        return mOngoingRequests;
    }

    public String store(StreamableContent data, int secondsUntilExipred)
            throws ClientProtocolException, IOException {

        MultipartHttpEntity multipart = new MultipartHttpEntity();
        multipart.addPart("upload", data);
        String url = ClientConfig.getFileCacheBaseUri() + "/?expires_in=" + secondsUntilExipred;
        HttpPost request = new HttpPost(sign(url));
        request.setEntity(multipart);
        HttpResponse response = getHttpClient().execute(request);

        return convertResponseToString(response);
    }

    public void fetch(String locationUri, StreamableContent data) throws ClientProtocolException,
            IOException {
        HttpGet request = new HttpGet(locationUri);
        HttpResponse response = getHttpClient().execute(request);

        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode != 200) {
            throw new HttpResponseException(statuscode, "Unexpected status code " + statuscode
                    + " while requesting " + locationUri);
        }

        InputStream is = response.getEntity().getContent();
        OutputStream storageStream = data.openOutputStream();
        byte[] buffer = new byte[0xFFFF];
        int len;
        while ((len = is.read(buffer)) != -1) {
            if (isFetchStopped) {
                return;
            }
            storageStream.write(buffer, 0, len);
        }
    }

    public String asyncStore(StreamableContent data, int secondsUntilExipred,
            HttpResponseHandler responseHandler) throws IOException {

        String uri = ClientConfig.getFileCacheBaseUri() + "/" + UUID.randomUUID();

        AsyncHttpPut storeRequest = new AsyncHttpPut(uri + "?expires_in=" + secondsUntilExipred);
        storeRequest.registerResponseHandler(responseHandler);
        storeRequest.setBody(data);
        storeRequest.start();

        return uri;
    }

    public void cancel(String uri) {
        AsyncHttpRequest request = mOngoingRequests.remove(uri);
        request.interrupt();
    }
}
