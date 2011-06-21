package com.hoccer.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

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
    static String MOD = "CryptoHelper";

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        return kp;
    }

    public static RSAPublicKeySpec getPublicKey(KeyPair kp) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
        return pub;
    }

    public static RSAPrivateKeySpec getPrivateKey(KeyPair kp) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
        return priv;
    }

    public static String toString(RSAPrivateKeySpec priv) {
        String result = "RSA-Private:" + "modulos:" + priv.getModulus() + ",exponent:"
                + priv.getPrivateExponent();
        return result;
    }

    public static String toString(RSAPublicKeySpec pub) {
        String result = "RSA-Public:" + "modulos:" + pub.getModulus() + ",exponent:"
                + pub.getPublicExponent();
        return result;
    }

    public static void testRSA() {
        try {
            byte[] testsalt = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
            Log.v(MOD, "testRSA-AES Key Generator Testing:");
            Log.v(MOD, "salt=" + Base64.encodeBytes(testsalt));
            Log.v(MOD, "salt=" + toHex(testsalt));
            // Log.v("SHA1(password)=", toHex(md("password", "SHA-1")));
            Cipher c = makeCipher(testsalt, "password", Cipher.ENCRYPT_MODE, "AES", 128, "SHA1PRNG");
            byte[] encrypted = crypt(c, "test".getBytes());
            Log.v(MOD, "AES-encrypted=" + Base64.encodeBytes(encrypted));
            Log.v(MOD, "AES-encrypted=" + toHex(encrypted));
            Cipher d = makeCipher(testsalt, "password", Cipher.DECRYPT_MODE, "AES", 128, "SHA1PRNG");
            byte[] decrypted = crypt(d, encrypted);
            Log.v(MOD, "AES-decrypted=" + new String(decrypted));
            Log.v(MOD, "done test");

            KeyPair kp = generateRSAKeyPair();
            Log.v(MOD, "RSA" + toString(getPrivateKey(kp)));
            Log.v(MOD, "RSA" + toString(getPublicKey(kp)));
            String encr = encryptRSA(kp.getPublic(), "blafasel12345678");
            Log.v(MOD, "RSA-encrypted:" + encr);
            String decr = decryptRSA(kp.getPrivate(), encr);
            Log.v(MOD, "RSA-decrypted:" + decr);
            Log.v(MOD, "RSA-pub-ts:" + Base64.encodeBytes(kp.getPublic().getEncoded()));
            Log.v(MOD, "RSA-priv-ts:" + Base64.encodeBytes(kp.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static byte[] sha1_mac(byte[] message, byte[] keyString)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec(keyString, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(message);
        return bytes;
    }

    public static String sha1_mac(String message, String keyString)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return new String(Base64.encodeBytes(sha1_mac(message.getBytes("UTF-8"),
                keyString.getBytes("UTF-8"))));
    }

    public static byte[] md(byte[] bytes, String algorithm) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1"); // Or any other algorithm.
        md.update(bytes);
        byte[] digest = md.digest();
        return digest;
    }

    public static byte[] md_sha1(byte[] bytes) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        return md(bytes, "SHA-1");
    }

    public static byte[] md(String raw, String algorithm) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        byte[] bytes = raw.getBytes("UTF-8"); // "8859_1"/* encoding */ );
        return md(bytes, algorithm);
    }

    public static byte[] decryptRSA(PrivateKey priv, byte[] encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priv);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] encryptRSA(PublicKey pub, byte[] clear) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static String decryptRSA(PrivateKey priv, String encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        return new String(decryptRSA(priv, toByte(encrypted)));
    }

    public static String encryptRSA(PublicKey pub, String clear) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        return toHex(encryptRSA(pub, clear.getBytes()));
    }

    /*
     * saveToFile("public.key", pub.getModulus(), pub.getPublicExponent());
     * saveToFile("private.key", priv.getModulus(), priv.getPrivateExponent());
     */
    /*
     * public static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws
     * Exception { ObjectOutputStream oout = new ObjectOutputStream( new BufferedOutputStream(new
     * FileOutputStream(fileName))); try { oout.writeObject(mod); oout.writeObject(exp); } catch
     * (Exception e) { throw new Exception("error", e); } finally { oout.close(); } }
     */

    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static byte[] shorten(byte[] array, int length) {
        byte[] result = new byte[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    // public static byte[] getRawKey2(byte[] password, String transformation, int keysize,
    // String random_algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    // byte[] raw = md(password, "SHA-1");
    // Log.v("SHA1-digest-key:", toHex(raw));
    // return raw;
    // }

    private static byte[] getRawKey2(byte[] salt, byte[] password, String transformation,
            int keysize, String random_algorithm) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        byte[] key = concat(password, salt);
        byte[] raw = shorten(md(key, "SHA-1"), keysize / 8);
        Log.v(MOD, "getRawKey2: salt=" + toHex(salt));
        Log.v(MOD, "getRawKey2: pass=" + toHex(password));
        Log.v(MOD, "getRawKey2: pre-key" + toHex(key));
        Log.v(MOD, "getRawKey2: SHA1-key:" + toHex(raw));
        return raw;
    }

    // public static byte[] getRawKey1(byte[] password, String transformation, int keysize,
    // String random_algorithm) throws NoSuchAlgorithmException {
    // KeyGenerator kgen = KeyGenerator.getInstance(transformation);
    // SecureRandom sr = SecureRandom.getInstance(random_algorithm);
    // sr.setSeed(password);
    // kgen.init(keysize, sr);
    // SecretKey skey = kgen.generateKey();
    // byte[] raw = skey.getEncoded();
    // Log.v("SHA1-kgen-key:", toHex(raw));
    // return raw;
    // }

    public static byte[] makeRandomSalt(int bits) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        // sr.setSeed(System.nanoTime());

        byte[] salt = new byte[bits / 8];
        sr.nextBytes(salt);
        // byte[] salt = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        return salt;
    }

    public static Cipher makeCipher(byte[] salt, String password, int mode, String transformation,
            int keysize, String random_algorithm) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException {
        Log.v(MOD, "makeCipher: salt=" + toHex(salt));
        Log.v(MOD, "makeCipher: password:" + password);
        Log.v(MOD, "makeCipher: mode=" + mode);
        Log.v(MOD, "makeCipher: transformation: " + transformation);
        Log.v(MOD, "makeCipher: keysize: " + keysize);
        Log.v(MOD, "makeCipher: random_algorithm: " + random_algorithm);
        byte[] rawKey = getRawKey2(salt, password.getBytes("UTF-8"), transformation, keysize,
                random_algorithm);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, transformation);
        Cipher cipher = Cipher.getInstance(transformation + "/CBC/PKCS7Padding");
        byte[] nulliv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        cipher.init(mode, skeySpec, new IvParameterSpec(nulliv));
        return cipher;
    }

    public static Cipher makeCipherECB(byte[] salt, String password, int mode,
            String transformation, int keysize, String random_algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {
        byte[] rawKey = getRawKey2(salt, password.getBytes("UTF-8"), transformation, keysize,
                random_algorithm);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, transformation);
        Cipher cipher = Cipher.getInstance(transformation + "/ECB/PKCS7Padding");
        cipher.init(mode, skeySpec);
        return cipher;
    }

    // public static Cipher makeDefaultCipher(String seed, int mode) throws
    // NoSuchAlgorithmException,
    // NoSuchPaddingException, InvalidKeyException {
    // return makeCipher(seed, mode, "AES", 128, "SHA1PRNG"); // 192 and 256 bits may not be
    // // available
    // }
    //
    // public static Cipher makeDefaultEncryptionCipher(String seed) throws
    // NoSuchAlgorithmException,
    // NoSuchPaddingException, InvalidKeyException {
    // return makeDefaultCipher(seed, Cipher.ENCRYPT_MODE);
    // }
    //
    // public static Cipher makeDefaultDecryptionCipher(String seed) throws
    // NoSuchAlgorithmException,
    // NoSuchPaddingException, InvalidKeyException {
    // return makeDefaultCipher(seed, Cipher.DECRYPT_MODE);
    // }

    public static String encrypt(byte[] salt, String password, String cleartext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException {

        Cipher c = makeCipher(salt, password, Cipher.ENCRYPT_MODE, "AES", 128, "SHA1PRNG");
        byte[] result = crypt(c, cleartext.getBytes());
        return Base64.encodeBytes(result);
    }

    public static String decrypt(byte[] salt, String password, String encrypted_b64)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException,
            InvalidAlgorithmParameterException {
        Cipher c = makeCipher(salt, password, Cipher.DECRYPT_MODE, "AES", 128, "SHA1PRNG");
        byte[] enrypted = Base64.decode(encrypted_b64);
        byte[] result = crypt(c, enrypted);
        return Base64.encodeBytes(result);

    }

    // private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException {
    // return getRawKey(seed, "AES", 128, "SHA1PRNG");
    //
    // }

    // private static byte[] getRawKey(byte[] seed, String transformation, int keysize,
    // String random_algorithm) throws NoSuchAlgorithmException {
    // KeyGenerator kgen = KeyGenerator.getInstance(transformation);
    // SecureRandom sr = SecureRandom.getInstance(random_algorithm);
    // sr.setSeed(seed);
    // kgen.init(keysize, sr);
    // SecretKey skey = kgen.generateKey();
    // byte[] raw = skey.getEncoded();
    // Log.v("SHA1-key:", toHex(raw));
    // return raw;
    // }

    public static byte[] crypt(Cipher cipher, byte[] clear) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    // private static byte[] decrypt(byte[] raw, byte[] encrypted) throws NoSuchPaddingException,
    // NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
    // IllegalBlockSizeException {
    // SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    // Cipher cipher = Cipher.getInstance("AES");
    // cipher.init(Cipher.DECRYPT_MODE, skeySpec);
    // byte[] decrypted = cipher.doFinal(encrypted);
    // return decrypted;
    // }

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
