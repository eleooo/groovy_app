package cs.b2b.core.common.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SecurityUtil {

	private static String DefaultEncryptKey = "B2BEDIEncryptKey";
	
	private byte[] md5SecretKey() throws Exception {
		MessageDigest md5 = null;
		byte[] pwdMd5 = null;
		byte[] pwd3Des = new byte[24];

		md5 = MessageDigest.getInstance("MD5");
		
		md5.update(DefaultEncryptKey.getBytes());
		pwdMd5 = md5.digest();
		md5.reset();
		
		if (pwdMd5.length > 24) {
			System.arraycopy(pwdMd5, 0, pwd3Des, 0, pwd3Des.length);
		} else {
			System.arraycopy(pwdMd5, 0, pwd3Des, 0, pwdMd5.length);
			int j = 0;
			for (int i = pwdMd5.length; i < 24; i++) {
				pwd3Des[i] = pwdMd5[j];
				j++;
			}
		}
		return pwd3Des;
	}
	
	public String PswEncrypt(String sEncString) throws Exception {

		String encrypted = null;
		
		byte[] pwd3Des = md5SecretKey();	
		SecretKeySpec sKeySpec = new SecretKeySpec(pwd3Des, "desede");
		
		Cipher ciper = Cipher.getInstance("desede/ECB/PKCS5Padding");
		ciper.init(Cipher.ENCRYPT_MODE, sKeySpec);
		byte[] result = ciper.doFinal(sEncString.getBytes());
		encrypted = new BASE64Encoder().encode(result);
		
		return encrypted;
	}

	public String PswDecrypt(String sEncString) throws Exception {
		String decrypted = null;
		byte[] pwd3Des = md5SecretKey();	
		SecretKeySpec sKeySpec = new SecretKeySpec(pwd3Des, "desede");
		
		Cipher ciper = Cipher.getInstance("desede/ECB/PKCS5Padding");
		ciper.init(Cipher.DECRYPT_MODE, sKeySpec);
		byte[] result = ciper.doFinal(new BASE64Decoder().decodeBuffer(sEncString));
		decrypted = new String(result);
		
		return decrypted;
	}

	
	/**
	 * new feature
	 * 
	 */
	
	private byte[] md5SecretKeyByInput(String inkey) throws Exception {
		MessageDigest md5 = null;
		byte[] pwdMd5 = null;
		byte[] pwd3Des = new byte[24];
		
		md5 = MessageDigest.getInstance("MD5");
		
		md5.update(inkey.getBytes());
		pwdMd5 = md5.digest();
		md5.reset();
		
		if (pwdMd5.length > 24) {
			System.arraycopy(pwdMd5, 0, pwd3Des, 0, pwd3Des.length);
		} else {
			System.arraycopy(pwdMd5, 0, pwd3Des, 0, pwdMd5.length);
			int j = 0;
			for (int i = pwdMd5.length; i < 24; i++) {
				pwd3Des[i] = pwdMd5[j];
				j++;
			}
		}
		return pwd3Des;
	}
	
	public String encryptWithKey(String sEncString, String inkey) throws Exception {
		String encrypted = null;
		
		byte[] pwd3Des = md5SecretKeyByInput(inkey);	
		SecretKeySpec sKeySpec = new SecretKeySpec(pwd3Des, "desede");
		
		Cipher ciper = Cipher.getInstance("desede/ECB/PKCS5Padding");
		ciper.init(Cipher.ENCRYPT_MODE, sKeySpec);
		byte[] result = ciper.doFinal(sEncString.getBytes());
		encrypted = new BASE64Encoder().encode(result);
		
		return encrypted;
	}
	
	public String decryptWithKey(String sEncString, String inkey) throws Exception {
		String decrypted = null;
		byte[] pwd3Des = md5SecretKeyByInput(inkey);
		SecretKeySpec sKeySpec = new SecretKeySpec(pwd3Des, "desede");
		
		Cipher ciper = Cipher.getInstance("desede/ECB/PKCS5Padding");
		ciper.init(Cipher.DECRYPT_MODE, sKeySpec);
		byte[] result = ciper.doFinal(new BASE64Decoder().decodeBuffer(sEncString));
		decrypted = new String(result);
		
		return decrypted;
	}
	
	///////////////
	
	public static void main(String[] args) throws Exception {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
