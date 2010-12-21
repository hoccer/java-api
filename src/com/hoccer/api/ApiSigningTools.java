package com.hoccer.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.hoccer.data.Base64;

public class ApiSigningTools {

    static public String sign(String url, String apiKey, String sharedSecret) {
        Date date = new Date();
        url = url + (url.contains("?") ? "&" : "?") + "api_key=" + apiKey + "&timestamp="
                + date.getTime() / 1000;

        String signature = digest(url, sharedSecret);

        try {
            return url + "&signature=" + URLEncoder.encode(signature, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "unsupported encoding"; // does not happen
        }
    }

    static String digest(String url, String secretKey) {

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
            return Base64.encodeBytes(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("bad signing");
    }
}
