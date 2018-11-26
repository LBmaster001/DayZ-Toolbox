package de.lbmaster.dayztoolbox.utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	private static final String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAktSARe0zzylpMEuBuIJ31O9tvL3XwKCDclKwwVkKeTjWzu9DXjeUZaatsmBdDWUIDy1FiWzRACzsOVFAAV4gSHGRtxyjniYwPtsBfeEBXvaeh2XmzlZJtVdUVG1xYJBxrIDLoXAvcyzi8ouyTOXgy3h/N7E/pJz6Q46eJj4vxLTz6KNQKYPwu/QcHQ+DoWjY/ZYryD8Isbj7oqcMMCaGhPh04O2PIYNFZnKmthVwC7Z6bgwTGAArSmMKEUIKLHHtu0hDqWsFvQO++k6vPlpVx1Hlfmavi8s1iPGY8bZOjTiq1LB5z8akIYCym8ChdZeQI14OxJQuey2yHmnueDpBnQIDAQAB";

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static byte[] encryptMessage(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
		generateKey();
		byte[] encKey = cipher.doFinal(key);
		byte[] encMess = encryptAES(bytes);
		System.out.println("Message Encrypted KeyLength: " + encKey.length + " AES KeyLength: " + key.length);
		return ByteUtilsBE.addArrays(encKey, encMess);
	}

	private static byte[] encryptAES(byte[] strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(strToEncrypt);
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static void generateKey() {
		byte[] aesKey = new byte[32];
		new Random().nextBytes(aesKey);

		key = aesKey;
		secretKey = new SecretKeySpec(key, "AES");
	}

	private static PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKey));
		return publicKey;
	}

}
