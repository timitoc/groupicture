package com.timitoc.groupic.utils;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Encryptor {

    private static final String ALGO = "AES";
    private static Cipher cipher;


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

}
