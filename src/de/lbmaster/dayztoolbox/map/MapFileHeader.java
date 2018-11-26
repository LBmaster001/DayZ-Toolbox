package de.lbmaster.dayztoolbox.map;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class MapFileHeader {

	public static final int HEADER_SIZE = 8;
	public static final int CONTENT_HEADER_SIZE = 10;

	private byte version;
	private int headerLength;
	private byte[] reserved = new byte[HEADER_SIZE - 5];
	private byte[] headerContent;

	public MapFileHeader(File file) throws IOException {
		byte[] header = new byte[HEADER_SIZE];
		readBytesFromFile(0, header, file);
		readHeaderBytes(header);
		readContentBytes(file);
	}
	
	public MapFileHeader(byte version, byte[] headerContent) {
		this.version = version;
		this.headerLength = headerContent.length;
		this.headerContent = headerContent;
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(HEADER_SIZE);
		buffer.write(version);
		buffer.write(ByteUtilsBE.intToBytes(headerLength));
		buffer.write(reserved);
		return buffer.toByteArray();
		
	}
	
	public int getContentStart() {
		return headerLength + HEADER_SIZE + 2;
	}
	
	public List<MapObjectHeader> getHeaders() {
		List<MapObjectHeader> headers = new ArrayList<MapObjectHeader>();
		if (headerContent != null && headerContent.length > 0) {
			short elementCount = ByteUtilsBE.readShort(headerContent, 0);
			int index = 2;
			int contentStart = getContentStart();
			for (int i = 0; i < elementCount; i++) {
				byte[] contentHead = ByteUtilsBE.substring(headerContent, index, CONTENT_HEADER_SIZE);
				index += CONTENT_HEADER_SIZE;
				MapObjectType type = MapObjectType.getTypeById(ByteUtilsBE.readShort(contentHead, 0));
				long length = ByteUtilsBE.readLong(contentHead, 2);
				System.out.println(type.toString() + " Start: " + contentStart + " " + length);
				headers.add(new MapObjectHeader(length, type, contentStart));
				contentStart += length;
			}
		}
		return headers;
	}
	
	private void readContentBytes(File file) throws IOException {
		headerContent = new byte[headerLength + 2];
		readBytesFromFile(HEADER_SIZE, headerContent, file);
	}

	private void readHeaderBytes(byte[] headerBytes) {
		this.version = headerBytes[0];
		this.headerLength = ByteUtilsBE.readInt(headerBytes, 1);
		this.reserved = ByteUtilsBE.substring(headerBytes, 5, 3);
	}

	private void readBytesFromFile(long start, byte[] array, File file) throws IOException {
		FileInputStream br = new FileInputStream(file);
		br.skip(start);
		br.read(array);
		br.close();
	}

	public byte getVersion() {
		return version;
	}

	public int getHeaderLength() {
		return headerLength;
	}

	public byte[] getReservedBytes() {
		return reserved;
	}
}
