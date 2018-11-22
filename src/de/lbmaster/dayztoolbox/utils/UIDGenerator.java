package de.lbmaster.dayztoolbox.utils;

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
}
