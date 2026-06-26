package com.iolite.kpcamera.attendance.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ParameterSettring {

    private static volatile ParameterSettring instance = null;

    private String iv = "@|-|-en(|@(e@|)|";
    private static String secretKeyString = "(#&!$t@ten(|@n(@";

    private IvParameterSpec ivSpec;
    private SecretKeySpec keySpec;
    private Cipher cipher;

    // Singleton - Default Instance
    public static ParameterSettring getInstance() {
        if (instance == null) {
            synchronized (ParameterSettring.class) {
                if (instance == null) {
                    instance = new ParameterSettring();
                }
            }
        }
        return instance;
    }

    // Singleton - Custom Instance
    public static ParameterSettring getInstance(String iv, String secretKey) {
        if (instance == null) {
            synchronized (ParameterSettring.class) {
                if (instance == null) {
                    instance = new ParameterSettring(iv, secretKey);
                }
            }
        }
        return instance;
    }

    // Default Constructor
    private ParameterSettring() {
        init(iv, secretKeyString);
    }

    // Parameterized Constructor
    private ParameterSettring(String iv, String secretKey) {
        init(iv, secretKey);
    }

    // Initialize cipher
    private void init(String iv, String secretKey) {
        try {
            this.ivSpec = new IvParameterSpec(iv.getBytes());
            this.keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException("Cipher initialization failed!", e);
        }
    }

    // AES CBC Encryption
    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.isEmpty())
            throw new Exception("Text cannot be empty");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("Error in encrypt(): " + e.getMessage());
        }
    }

    // Convert byte[] to hex string
    public static String byteArrayToHexString(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    // Manual padding
    private static String padString(String source) {
        int size = 16;
        int padLength = size - (source.length() % size);
        return source + repeat('\0', padLength);
    }
    
    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // ---------- AES ECB STATIC ENCRYPTION -----------

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static void setKey(String myKey) {
        try {
            key = myKey.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");

            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // 128-bit key

            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error setting key: ", e);
        }
    }

    // AES ECB Encryption (Base64 URL-safe)
    public static String encrypt(String text, String secret) {
        try {
            if (secret == null || secret.isEmpty()) {
                secret = secretKeyString;
            }
            setKey(secret);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));

        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
        }
        return null;
    }
}

