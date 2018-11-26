package de.lbmaster.dayztoolbox.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class UIDGenerator {

	
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	public static String generate64UID() {
		return generateUID(64);
	}
	
	public static String generateUID(int uidlength) {
		String uid = "";
		Random r = new Random();
		for (int i = 0; i < uidlength; i++) {
			uid += chars.charAt(r.nextInt(chars.length()));
		}
		return uid;
	}
	
	public static String getBIUID(String steamid) {
		if (steamid.trim().length() != 17) {
			steamid = (steamid + "000000000000000").substring(0, 17);
		}
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			return Base64.getEncoder().encodeToString(sha256.digest(steamid.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error while Parsing SteamID \"" + steamid + "\" to UID");
			e.printStackTrace();
		}
		return null;
		
	}
}
