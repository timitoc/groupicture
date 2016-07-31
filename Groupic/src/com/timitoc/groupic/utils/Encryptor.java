package com.timitoc.groupic.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Encryptor {

    public static String hash(String toHash){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(toHash.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return Base64.encodeBytes(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("Failed to hash String");
    }

    public static byte[] hashBytes(byte[] toHash) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(toHash);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int saltSize = 5;

    public static String saltedHash(String toHash) {
        return saltedHash(toHash, null);
    }

    private static String saltedHash(String toHash, byte[] saltBytes) {
        Random r = new Random();
        if (saltBytes == null) {
            saltBytes = new byte[saltSize];
            r.nextBytes(saltBytes);
        }
        byte[] textBytes = toHash.getBytes();
        byte[] textWithSaltBytes = new byte[textBytes.length + saltBytes.length];
        System.arraycopy(textBytes, 0, textWithSaltBytes, 0, textBytes.length);
        System.arraycopy(saltBytes, 0, textWithSaltBytes, textBytes.length, saltBytes.length);
        byte[] hashBytes = Encryptor.hashBytes(textWithSaltBytes);
        byte[] hashWithSaltBytes = new byte[hashBytes.length + saltBytes.length];
        System.arraycopy(hashBytes, 0, hashWithSaltBytes, 0, hashBytes.length);
        System.arraycopy(saltBytes, 0, hashWithSaltBytes, hashBytes.length, saltBytes.length);
        return Base64.encodeBytes(hashWithSaltBytes);
    }

    public static boolean checkSaltedHash(String text, String hash) {
        try {
            byte[] hashWithSaltBytes = Base64.decode(hash);
            byte[] saltBytes = new byte[hashWithSaltBytes.length - 32];
            System.arraycopy(hashWithSaltBytes, 32, saltBytes, 0, saltBytes.length);
            String expectedHash = saltedHash(text, saltBytes);
            return expectedHash.equals(hash);

        } catch (IOException e) {
            return false;
        }
    }
}
