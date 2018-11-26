package de.lbmaster.dayztoolbox.utils;

public class ByteUtilsLE {
	
	public static double readDouble(byte[] bytes, int pos) {
		return Double.longBitsToDouble(readLong(bytes, pos));
	}
	
	public static float readFloat(byte[] bytes, int pos) {
		return Float.intBitsToFloat(readInt(bytes, pos));
	}

	public static long readLong(byte[] bytes, int pos) {
		byte[] bytess = substring(bytes, pos, 8);
	    long result = 0;
	    for (int i = 7; i >= 0; i--) {
	        result <<= 8;
	        result |= (bytess[i] & 0xFF);
	    }
	    return result;
	}

	public static int readInt(byte[] bytes, int pos) {
		byte[] bytess = substring(bytes, pos, 4);
		return bytess[3] << 24 | (bytess[2] & 0xFF) << 16 | (bytess[1] & 0xFF) << 8 | (bytess[0] & 0xFF);

	}

	public static short readShort(byte[] bytes, int pos) {
		byte[] bytess = substring(bytes, pos, 2);
		return (short) ((bytess[1] & 0xFF) << 8 | (bytess[0] & 0xFF));
	}
	
	public static byte[] floatToBytes(float value) {
		return intToBytes(Float.floatToIntBits(value));
	}
	
	public static byte[] doubleToBytes(double value) {
		return longToBytes(Double.doubleToLongBits(value));
	}

	public static byte[] longToBytes(long value) {
	    byte[] result = new byte[8];
	    for (int i = 0; i < 8; i++) {
	        result[i] = (byte)(value & 0xFF);
	        value >>= 8;
	    }
	    return result;
	}

	public static char[] longToChars(long value) {
		char[] result = new char[8];
	    for (int i = 0; i < 8; i++) {
	        result[i] = (char)(value & 0xFF);
	        value >>= 8;
	    }
	    return result;
	}
	
	public static byte[] intToBytes(int value) {
	    return new byte[] { 
	            (byte)(value),
	            (byte)(value >> 8),
	            (byte)(value >> 16),
	            (byte)(value >> 24)};
	}

	public static byte[] shortToBytes(short value) {
	    return new byte[] { 
	            (byte)value,
	            (byte) (value >> 8)};
	}

	public static byte[] substring(byte[] array, int start, int length) {
		if (length < 0)
			throw new IllegalArgumentException("Length must be greater than zero !");
		byte[] arr = new byte[length];
		for (int i = 0; i < length; i++) {
			if (i + start >= array.length)
				arr[i] = 0x00;
			else
				arr[i] = array[i + start];
		}
		return arr;
	}

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	public static byte[] addArrays(byte[] array1, byte[] array2) {
		byte[] array3 = new byte[array1.length + array2.length];
		for (int i = 0; i < array1.length; i++) {
			array3[i] = array1[i];
		}
		for (int i = 0; i < array2.length; i++) {
			array3[i + array1.length] = array2[i];
		}
		return array3;
	}
	public static char[] addArrays(char[] array1, char[] array2) {
		char[] array3 = new char[array1.length + array2.length];
		for (int i = 0; i < array1.length; i++) {
			array3[i] = array1[i];
		}
		for (int i = 0; i < array2.length; i++) {
			array3[i + array1.length] = array2[i];
		}
		return array3;
	}
}
