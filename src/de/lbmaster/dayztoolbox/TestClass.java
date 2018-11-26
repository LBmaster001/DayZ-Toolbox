package de.lbmaster.dayztoolbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import de.lbmaster.dayz.playerdb.Data;
import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;
import de.lbmaster.dayztoolbox.utils.UIDGenerator;

public class TestClass {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		int test = ((int) (byte) 0xFF);
		System.out.println(test);
		System.out.println(new Data(ByteUtilsBE.hexStringToByteArray("00001041")).getFloat());
		long startID = Long.parseLong("76561198000000000");
		long currentID = startID;
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		long start = System.currentTimeMillis();
		int ids = 10000000;
		List<String> searchIDs = new ArrayList<String>();
		searchIDs.add("4e6J9rBov9EChLGlp+ckzyAbUh/tvlzXXBrFR7pSS+4=");
		searchIDs.add("mIexbZ+MI4eloOBxytc2g9GQYpmeKV+uDCHTGDmkZic=");
		searchIDs.add("v91G79ykVGId68+eFwlTafnStB+1fJqOZAVb7l1nMvM=");
		searchIDs.add("lzcuSmwb2Bqq3gxAdqtqH+nDguVPTzc45MgMgI3y9p4=");
		searchIDs.add("cEG3L9Aamy07yrywgWdYdb+8d1h+JaY24cme+Lnv8M8=");
		searchIDs.add("2vsSi/simaxGkcJleen2lQvo4JDHeXncWjAF07WYOVA=");
		searchIDs.add("7DdV7AAc4zYDYhcA2clV0B2WmVb9kuerMf/D6r2Ndc8=");
		searchIDs.add("qbRLXNneCBsY7WrNjM/s5wCiF64nedDmVXCpAGrAykk=");
		searchIDs.add("r1QZXLR1L3YgNbFOanSFLlb7Qn+xazfMtzv2CxnLx6M=");
		searchIDs.add("RCTACJwKHG65eP5lRg+IY2I/Vc3IuUGDmshgFP3XSvQ=");
		searchIDs.add("jrY6MU9FzykHg69mVa4wmwygKBEn51DoX76gUm2IC9M=");
		searchIDs.add("wcUjuYJ8WUFulXhP9hzbPJATJiqnhkPgagD6hzK+N64=");
		FileWriter fw = new FileWriter(new File("out.txt"));
		for (int i = 0; i < ids; i++) {
			String id = (Long.toString(currentID) + "000000000").substring(0, 17);
//			if (searchIDs.contains(biuid)) {
//				System.out.println(biuid + " -> " + id);
//			}
			fw.write(ByteUtilsBE.addArrays(ByteUtilsBE.longToChars(currentID), bytesToChars(sha256.digest(id.getBytes()))));
			
			if (currentID % 10000 == 0)
				fw.flush();
			currentID++;
		}
		fw.flush();
		fw.close();
		long stop = System.currentTimeMillis();
		System.out.println((stop - start) + "ms for " + ids + " ids");
		System.out.println("Processed IDs from " + startID + " to " + (currentID-1));
		System.out.println(UIDGenerator.getBIUID("76561198141000000"));
	}
	
	private static char[] bytesToChars(byte[] bytes) {
		char[] chars = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			chars[i] = (char) bytes[i];
		return chars;
	}
}
