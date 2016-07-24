package utils;

import Global.Constants;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.random;
import static java.lang.StrictMath.random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
	
	private static final String ALGO = "AES";
	private static Cipher cipher;
	static byte keyBytes[] = Constants.KEY_BYTES;
	
	public static String encrypt(String s) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(keyBytes, ALGO);
		cipher = Cipher.getInstance(ALGO);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = cipher.doFinal(s.getBytes());
		String encryptedValue = Base64.encode(encVal);
                return encryptedValue;
//return encryptedValue.substring(0, encryptedValue.length()-1);
	}
	
	public static String decrypt(String s) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(keyBytes, ALGO);
		cipher = Cipher.getInstance(ALGO);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] b = Base64.decode(s);
		return new String(cipher.doFinal(b));
	}
        
        public static String hash(String toHash){
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(toHash.getBytes("UTF-8"));
                byte[] digest = md.digest();
                return Base64.encode(digest);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new RuntimeException("Failed to hash String");
        }
        
        
        public static String generateRandomString(int nr) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder();
            while(sb.length() < nr){
                sb.append(Integer.toHexString(r.nextInt()));
            }
            return sb.toString().substring(0, nr);
        }
}
