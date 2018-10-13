package de.lbmaster.dayztoolbox.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.lbmaster.dayztoolbox.utils.ByteUtils;

public class MapPosition {

	double x;
	double y;
	double z;
	String name;

	public MapPosition(double x, double y, double z, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return ((this.name != null ? name + " " : "") + "X:" + x + " Y:" + y + " Z:" + z);
	}

	public MapPosition(double x, double y, double z) {
		this(x, y, z, null);
	}

	public MapPosition(double x, double z) {
		this(x, 0, z);
	}

	public MapPosition(byte[] data) {
		byte type = data[0];
		int pos = 1;
		if ((type & 0x01) == 0x01) {
			this.x = ByteUtils.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x02) == 0x02) {
			this.y = ByteUtils.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x04) == 0x04) {
			this.z = ByteUtils.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x08) == 0x08) {
			short length = ByteUtils.readShort(data, pos);
			pos += 2;
			this.name = new String(ByteUtils.substring(data, pos, length));
		}
	}

	public byte[] toBytes() throws IOException {
		byte type = 0;
		type = (byte) (type | (x != 0 ? 0x01 : 0x00));
		type = (byte) (type | (y != 0 ? 0x02 : 0x00));
		type = (byte) (type | (z != 0 ? 0x04 : 0x00));
		type = (byte) (type | (name != null ? 0x08 : 0x00));
		System.out.println("Type: " + type);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bytes.write(new byte[] {type});
		if (x != 0)
			bytes.write(ByteUtils.doubleToBytes(x));
		if (y != 0)
			bytes.write(ByteUtils.doubleToBytes(y));
		if (z != 0)
			bytes.write(ByteUtils.doubleToBytes(z));
		if (name != null) {
			bytes.write(ByteUtils.shortToBytes((short) name.length()));
			bytes.write(name.getBytes());
		}
		return bytes.toByteArray();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}