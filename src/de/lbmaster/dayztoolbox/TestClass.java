package de.lbmaster.dayztoolbox;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;
import de.lbmaster.dayztoolbox.utils.ByteUtilsLE;

public class TestClass {

	public static void main(String[] args) {
		String bytes = "00000040";
		byte[] bytesLE = ByteUtilsLE.hexStringToByteArray(bytes);
		byte[] bytesBE = ByteUtilsBE.hexStringToByteArray(bytes);
		int index = 0;
		System.out.println(ByteUtilsLE.readFloat(bytesLE, index));
		System.out.println(ByteUtilsLE.readInt(bytesLE, index));
		System.out.println(ByteUtilsLE.readShort(bytesLE, index));
		System.out.println(ByteUtilsBE.readFloat(bytesBE, index));
		System.out.println(ByteUtilsBE.readInt(bytesBE, index));
		System.out.println(ByteUtilsBE.readShort(bytesBE, index));
		System.out.println();
		System.out.println(Math.round(ByteUtilsLE.readFloat(bytesLE, index)));
		System.out.println(Math.round(ByteUtilsLE.readInt(bytesLE, index)));
		System.out.println(Math.round(ByteUtilsBE.readFloat(bytesBE, index)));
		System.out.println(Math.round(ByteUtilsBE.readInt(bytesBE, index)));
	}
}
