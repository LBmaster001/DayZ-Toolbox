package de.lbmaster.dayz.playerdb;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;
import de.lbmaster.dayztoolbox.utils.ByteUtilsLE;

public class Data {

	private AtomicInteger index = new AtomicInteger(0);
	private byte[] content = new byte[0];
	
	public Data(byte[] data, boolean hasLengthHeader, int length) {
		if (hasLengthHeader) {
			this.content = data;
			length = getInt();
		}
		length = Math.min(data.length, length + index.get());
		this.content = Arrays.copyOfRange(data, 0, length);
	}
	
	public Data(byte[] data, boolean hasLengthHeader) {
		this(data, hasLengthHeader, data.length);
	}
	
	public Data(byte[] data) {
		this(data, false);
	}
	
	public Data(byte[] data, int length) {
		this(data, false, length);
	}
	
	public void skipToByte(byte b) {
		int index = new String(content).indexOf((char) b, this.index.get());
		if (index != -1)
			this.index.set(index);
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public byte[] getRemainingBytes() {
		return Arrays.copyOfRange(content, index.get(), content.length);
	}
	
	public int getRemainingBytesCount() {
		return getRemainingBytes().length;
	}
	
	public boolean isAtEnd() {
		return getRemainingBytesCount() <= 0;
	}
	
	public Data getData() {
		int length = getInt();
		Data dta = new Data(getRemainingBytes(), false, length);
		index.addAndGet(length);
		return dta;
	}
	
	public String toHexString() {
		return ByteUtilsBE.bytesToHex(getRemainingBytes());
	}
	
	public int getLength() {
		return content.length;
	}
	
	public void gotoIndexOfChar(char ch) {
		index.set(getIndexOfChar(ch));
	}
	
	public void gotoPosition(int pos) {
		index.set(pos);
	}
	
	public int getPosition() {
		return index.get();
	}
	
	public void skipBytes(int count) {
		index.addAndGet(count);
	}
	
	public String getNumber() {
		byte type = content[index.get()];
		index.incrementAndGet();
		if (type == (byte) 0xCA) {
			return getFloat() + "";
		} else if (type == (byte) 0xD7) {
			return getByte() + "";
		} else if (type == (byte) 0xC9) {
			return getShort() + "";
		} else {
			return 0 + "";
		}
	}
	
	public int getIndexOfChar(char value) {
		String s = new String(content);
		return s.indexOf(value, index.get());
	}

	public float getFloat() {
		byte[] floatBytes = Arrays.copyOfRange(content, index.get(), index.addAndGet(4));
		return ByteUtilsLE.readFloat(floatBytes,0);
	}
	
	public float getFloatWithPrefix(float ifNotFoundReturn) {
		if (nextByteIsEqualTo((byte) 0xCA)) {
			skipBytes(1);
			return getFloat();
		} else {
			return ifNotFoundReturn;
		}
	}

	public short getShort() {
		byte[] shortBytes = Arrays.copyOfRange(content, index.get(), index.addAndGet(2));
		return ByteUtilsLE.readShort(shortBytes, 0);
	}
	
	public byte getByte() {
		return content[index.getAndIncrement()];
	}

	public int getInt() {
		byte[] intBytes = Arrays.copyOfRange(content, index.get(), index.addAndGet(4));
		return ByteUtilsLE.readInt(intBytes, 0);
	}
	
	public String getString(int length) {
		return new String(Arrays.copyOfRange(content, index.get(), index.addAndGet(length)));
	}
	
	public String getString() {
		return getString(getByte());
	}
	
	public String getHexString(int length) {
		return ByteUtilsBE.bytesToHex(Arrays.copyOfRange(content, index.get(), index.addAndGet(length)));
	}

	public String findNullTerminatedString(String s) {
		int index = s.indexOf(0x00);
		if (index == -1)
			return null;
		return s.substring(this.index.get(), this.index.addAndGet(index)-1);
	}

	public String findNullTerminatedString() {
		return findNullTerminatedString(new String(content));
	}
	
	public boolean nextByteIsEqualTo(byte b) {
		return content[index.get()] == b;
	}

}
