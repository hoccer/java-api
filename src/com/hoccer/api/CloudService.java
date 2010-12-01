package com.hoccer.api;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.http.*;
import org.apache.http.conn.*;
import org.apache.http.conn.params.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;

import com.sun.org.apache.xml.internal.security.utils.*;

public class CloudService {

    private DefaultHttpClient         mHttpClient;
    protected final ClientConfig mConfig;

    public CloudService(ClientConfig config) {
        mConfig = config;
        setupHttpClient();
    }

    protected void setupHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        mHttpClient = new DefaultHttpClient(cm, httpParams);
        mHttpClient.getParams().setParameter("http.useragent", mConfig.getApplicationName());
    }

    private JSONObject convertResponseToJsonObject(HttpResponse response) throws ParseException,
            IOException, JSONException, UpdateException {
        String body = convertResponseToString(response);
        return new JSONObject(body);
    }

    protected JSONArray convertResponseToJsonArray(HttpResponse response) throws ParseException,
            IOException, JSONException, UpdateException {
        String body = convertResponseToString(response);
        return new JSONArray(body);
    }

    protected String convertResponseToString(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new ParseException("server respond with status code "
                    + response.getStatusLine().getStatusCode());
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ParseException("http body was empty");
        }
        long len = entity.getContentLength();
        if (len > 2048) {
            throw new ParseException(
                    "http body is to big and must be streamed (max is 2048, but was " + len
                            + " byte)");
        }

        String body = EntityUtils.toString(entity);
        return body;
    }

    protected DefaultHttpClient getHttpClient() {
        return mHttpClient;
    }

    protected String sign(String url) {
        Date date = new Date();
        url = url + "&api_key=" + mConfig.getApiKey() + "&timestamp=" + date.getTime() / 1000;

        String signature = digest(url, mConfig.getSharedSecret());

        try {
            return url + "&signature=" + URLEncoder.encode(signature, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "unsupported encoding"; // does not happen
        }
    }

    protected String digest(String url, String secretKey) {

        // get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");

        // get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(url.getBytes());
            // base64-encode the hmac
            return Base64.encode(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("keks");
    }
}
