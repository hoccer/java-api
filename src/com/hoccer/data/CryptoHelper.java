package com.hoccer.data;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Usage:
 * 
 * <pre>
 * String crypto = CryptoHelper.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = CryptoHelper.decrypt(masterpassword, crypto)
 * </pre>
 * 
 * @author Pavel Mayer, based on example by ferenc.hechler
 */

public class CryptoHelper {

    public static Cipher makeCipher(String seed, int mode, String transformation, int keysize,
            String random_algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException {
        byte[] rawKey = getRawKey(seed.getBytes(), transformation, keysize, random_algorithm);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, transformation);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(mode, skeySpec);
        return cipher;
    }

    public static Cipher makeDefaultCipher(String seed, int mode) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        return makeCipher(seed, mode, "AES", 128, "SHA1PRNG"); // 192 and 256 bits may not be
                                                               // available
    }

    public static Cipher makeDefaultEncryptionCipher(String seed) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        return makeDefaultCipher(seed, Cipher.ENCRYPT_MODE);
    }

    public static Cipher makeDefaultDecryptionCipher(String seed) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        return makeDefaultCipher(seed, Cipher.DECRYPT_MODE);
    }

    public static String encrypt(String seed, String cleartext) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException {
        return getRawKey(seed, "AES", 128, "SHA1PRNG");

    }

    private static byte[] getRawKey(byte[] seed, String transformation, int keysize,
            String random_algorithm) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(transformation);
        SecureRandom sr = SecureRandom.getInstance(random_algorithm);
        sr.setSeed(seed);
        kgen.init(keysize, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
