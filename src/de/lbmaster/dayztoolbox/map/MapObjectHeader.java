package de.lbmaster.dayztoolbox.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class MapObjectHeader {

	private long contentSize;
	private long contentStartIndex = 0;
	private MapObjectType type;

	public MapObjectHeader(long size, MapObjectType type, long contentStartIndex) {
		this.contentSize = size;
		this.type = type;
		this.contentStartIndex = contentStartIndex;
	}
	
	public byte[] toBytes() throws IOException {
		short type = this.type.getID();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(MapFileHeader.CONTENT_HEADER_SIZE);
		buffer.write(ByteUtilsBE.shortToBytes(type));
		buffer.write(ByteUtilsBE.longToBytes(contentSize));
		System.out.println("Type: " + type + " Size: " + contentSize);
		return buffer.toByteArray();
	}
	
	public long getContentStartIndex() {
		return contentStartIndex;
	}
	
	public MapObjectType getType() {
		return type;
	}

	public long getContentSize() {
		return contentSize;
	}
}
