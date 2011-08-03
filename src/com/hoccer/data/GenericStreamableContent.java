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
package com.hoccer.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GenericStreamableContent implements StreamableContent {

    static private final String         LOG_TAG           = "GenericStreamableContent";

    String                              mFilename         = "filename.unknown";
    String                              mContentType      = "unknown";

    private final ByteArrayOutputStream mDataStream       = new ByteArrayOutputStream();

    private boolean                     mCryptoEnabled    = false;
    private String                      mCryptoMethod     = CryptoHelper.getDefaultCrypto();
    private byte[]                      mCryptoSalt       = null;
    private byte[]                      mCryptoKeyphrase  = null;

    private int                         mCryptoKeySize    = CryptoHelper.getDefaultKeySize();
    private String                      mCryptoHash       = CryptoHelper.getDefaultHash();

    private OutputStream                mNewOutputStream  = null;
    private InputStream                 mNewInputStream   = null;
    private Cipher                      mEncryptionCipher = null;
    private Cipher                      mDecryptionCipher = null;

    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    public byte[] getCryptoKeyphrase() {
        return mCryptoKeyphrase;
    }

    @Override
    public InputStream openRawInputStream() throws IOException {
        if (mNewOutputStream != null) {
            mNewOutputStream.close();
            mNewOutputStream = null;
        }
        return new ByteArrayInputStream(mDataStream.toByteArray());
    }

    public long getRawStreamLength() throws IOException {
        return mDataStream.size();
    }

    @Override
    public OutputStream openRawOutputStream() throws IOException {
        if (mNewInputStream != null) {
            mNewInputStream.close();
            mNewInputStream = null;
        }
        return mDataStream;
    }

    @Override
    public OutputStream openNewOutputStream() throws IOException, Exception {
        mNewOutputStream = wrapOutputStream(openRawOutputStream());
        return mNewOutputStream;
    }

    @Override
    public InputStream openNewInputStream() throws IOException, Exception {
        mNewInputStream = wrapInputStream(openRawInputStream());
        return mNewInputStream;
    }

    @Override
    public long getNewStreamLength() throws IOException {
        return calcEncryptedSize((int) (getRawStreamLength()));
    }

    @Override
    public String toString() {
        if (mContentType != null
                && (mContentType.contains("text") || mContentType.contains("json"))) {
            return mDataStream.toString();
        }

        return mFilename + " (" + mContentType + ")";
    }

    public void setEncryption(String method, byte[] passphrase, int keysize, byte[] salt,
            String hash) {
        Log.v(LOG_TAG, "setEncryption method=" + method);
        Log.v(LOG_TAG, "setEncryption key=" + Base64.encodeBytes(passphrase));
        Log.v(LOG_TAG, "setEncryption keysize=" + keysize);
        Log.v(LOG_TAG, "setEncryption salt=" + Base64.encodeBytes(salt));
        mCryptoMethod = method;
        mCryptoKeyphrase = passphrase;
        mCryptoSalt = salt;
        mCryptoHash = hash;
        mCryptoEnabled = true;
    }

    public void setEncryption(JSONObject data, byte[] passphrase) throws IOException, JSONException {
        byte[] salt = Base64.decode(data.getString("salt"));
        setEncryption(data.getString("method"), passphrase, data.getInt("keysize"), salt,
                data.getString("hash"));
    }

    public void setEncryption(byte[] passphrase) {
        Log.v(LOG_TAG, "setEncryption key=" + Base64.encodeBytes(passphrase));
        mCryptoMethod = CryptoHelper.getDefaultCrypto();
        mCryptoKeyphrase = passphrase;
        mCryptoKeySize = CryptoHelper.getDefaultKeySize();
        mCryptoHash = CryptoHelper.getDefaultHash();
        mCryptoSalt = CryptoHelper.makeRandomBytes(mCryptoKeySize / 8);
        mCryptoEnabled = true;
    }

    public void setEncryption(GenericStreamableContent likeThis) {
        mCryptoMethod = likeThis.mCryptoMethod;
        mCryptoKeyphrase = likeThis.mCryptoKeyphrase;
        mCryptoKeySize = likeThis.mCryptoKeySize;
        mCryptoSalt = likeThis.mCryptoSalt;
        mCryptoHash = likeThis.mCryptoHash;
        mCryptoEnabled = likeThis.mCryptoEnabled;

        Log.v(LOG_TAG, "setEncryption like:" + likeThis.toString());
        Log.v(LOG_TAG, "setEncryption method=" + mCryptoMethod);
        Log.v(LOG_TAG, "setEncryption key=" + Base64.encodeBytes(mCryptoKeyphrase));
        Log.v(LOG_TAG, "setEncryption keysize=" + mCryptoKeySize);
        Log.v(LOG_TAG, "setEncryption hash=" + mCryptoHash);
        Log.v(LOG_TAG,
                "setEncryption salt="
                        + (mCryptoSalt != null ? Base64.encodeBytes(mCryptoSalt) : "null"));
        Log.v(LOG_TAG, "setEncryption enabled=" + mCryptoEnabled);
    }

    public void disableEncryption() throws IOException {
        Log.v(LOG_TAG, "wrapOutputStream disableEncryption(), this:" + this.toString());
        // Thread.dumpStack();
        mCryptoEnabled = false;
        if (mNewOutputStream != null) {
            mNewOutputStream.close();
            mNewOutputStream = null;
        }

        if (mNewInputStream != null) {
            mNewInputStream.close();
            mNewInputStream = null;
        }
    }

    public Cipher getCipher(int mode) throws Exception {
        return CryptoHelper.makeCipher(mCryptoSalt, mCryptoKeyphrase, mode, mCryptoMethod,
                mCryptoKeySize, mCryptoHash);
    }

    public byte[] getRawKey() {
        try {
            return CryptoHelper.getRawKey(mCryptoSalt, mCryptoKeyphrase, mCryptoMethod,
                    mCryptoKeySize, mCryptoHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public boolean encryptionEnabled() {
        return mCryptoEnabled;
    }

    public InputStream wrapInputStream(InputStream is) throws Exception {
        Log.v(LOG_TAG,
                "wrapInputStream encryption=" + encryptionEnabled() + ", this:" + this.toString());
        // Thread.dumpStack();
        if (encryptionEnabled()) {
            mEncryptionCipher = getCipher(Cipher.ENCRYPT_MODE);
            return new CipherInputStream(is, mEncryptionCipher);
        }
        mEncryptionCipher = null;
        return is;
    }

    protected int calcEncryptedSize(int plainSize) {
        if (mEncryptionCipher != null) {
            return mEncryptionCipher.getOutputSize(plainSize);
        }
        return plainSize;
    }

    public OutputStream wrapOutputStream(OutputStream os) throws Exception {
        Log.v(LOG_TAG,
                "wrapOutputStream encryption=" + encryptionEnabled() + ", this:" + this.toString());
        // Thread.dumpStack();
        if (encryptionEnabled()) {
            mDecryptionCipher = getCipher(Cipher.DECRYPT_MODE);
            return new CipherOutputStream(os, mDecryptionCipher);
        }
        mDecryptionCipher = null;
        return os;
    }

    public String encrypt(String data) throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {
        if (encryptionEnabled()) {
            return CryptoHelper.encrypt(mCryptoSalt, mCryptoKeyphrase, data);
        }
        return data;
    }

    public String decrypt(String data) throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, IOException,
            InvalidAlgorithmParameterException {
        if (encryptionEnabled()) {
            return CryptoHelper.decrypt(mCryptoSalt, mCryptoKeyphrase, data);
        }
        return data;
    }

    public void addEncryptionInfoIfNeccessary(JSONObject data) throws JSONException {
        if (encryptionEnabled()) {
            JSONObject encr = new JSONObject();
            encr.put("method", mCryptoMethod);
            encr.put("keysize", mCryptoKeySize);
            encr.put("salt", Base64.encodeBytes(mCryptoSalt));
            encr.put("hash", mCryptoHash);
            data.put("encryption", encr);
        }
    }

    public void putEncryptionInfo(JSONObject data) throws JSONException {
        JSONObject encr = new JSONObject();
        encr.put("method", mCryptoMethod);
        encr.put("keysize", mCryptoKeySize);
        encr.put("salt", Base64.encodeBytes(mCryptoSalt));
        encr.put("hash", mCryptoHash);
        data.put("encryption", encr);
    }

}
