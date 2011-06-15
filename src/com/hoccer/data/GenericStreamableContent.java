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

    String                              mFilename         = "filename.unknown";
    String                              mContentType;

    private final ByteArrayOutputStream mDataStream       = new ByteArrayOutputStream();

    private String                      mCryptoMethod     = null;
    private String                      mCryptoKey        = null;
    // private String mCryptoMethod = "AES";
    // private String mCryptoKey = "Hallo";
    // private String mCryptoKey = "Hallo2";

    private int                         mCryptoKeySize    = 128;
    private OutputStream                mNewStream        = null;
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

    protected InputStream openRawInputStream() throws IOException {
        return new ByteArrayInputStream(mDataStream.toByteArray());
    }

    public long getRawStreamLength() throws IOException {
        return mDataStream.size();
    }

    protected OutputStream openRawOutputStream() throws IOException {
        return mDataStream;
    }

    @Override
    public OutputStream openNewOutputStream() throws IOException, Exception {
        mNewStream = wrapOutputStream(openRawOutputStream());
        return mNewStream;
    }

    @Override
    public InputStream openNewInputStream() throws IOException, Exception {
        return wrapInputStream(openRawInputStream());
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

    public void setEncryption(String method, String key, int keysize) {
        mCryptoMethod = method;
        mCryptoKey = key;
    }

    public void setEncryption(String key) {
        mCryptoMethod = "AES";
        mCryptoKey = key;
        mCryptoKeySize = 128;
    }

    public void disableEncryption() {
        mCryptoMethod = null;
    }

    public Cipher getCipher(int mode) throws Exception {
        return CryptoHelper.makeCipher(mCryptoKey, mode, mCryptoMethod, mCryptoKeySize, "SHA1PRNG");
    }

    public boolean encryption() {
        return mCryptoMethod != null;
    }

    public InputStream wrapInputStream(InputStream is) throws Exception {
        Log.v("wrapInputStream", "encryption=" + encryption());
        if (encryption()) {
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
        Log.v("wrapOutputStream", "encryption=" + encryption());
        if (encryption()) {
            mDecryptionCipher = getCipher(Cipher.DECRYPT_MODE);
            return new CipherOutputStream(os, mDecryptionCipher);
        }
        mDecryptionCipher = null;
        return os;
    }

    public String encrypt(String data) throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        if (encryption()) {
            return CryptoHelper.encrypt(mCryptoKey, data);
        }
        return data;
    }

    public String decrypt(String data) throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        if (encryption()) {
            return CryptoHelper.decrypt(mCryptoKey, data);
        }
        return data;
    }

    public void addEncryptionInfo(JSONObject data) throws JSONException {
        if (encryption()) {
            data.put("encryption", mCryptoMethod);
            data.put("keysize", mCryptoKeySize);
        }
    }

}
