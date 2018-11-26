package de.lbmaster.dayztoolbox.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class MapPosition {

	double x;
	double y;
	double z;
	double a;
	String name;
	private MapPositions parent;

	public MapPosition(double x, double y, double z, double a, String name, MapPositions parent) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return ((this.name != null ? name + " " : "") + "X:" + x + " Y:" + y + " Z:" + z + " A:" + a);
	}

	public MapPosition(double x, double y, double z, double a, MapPositions parent) {
		this(x, y, z, a, null, parent);
	}

	public MapPosition(double x, double y, double z, MapPositions parent) {
		this(x, y, z, 0, null, parent);
	}

	public MapPosition(double x, double z, MapPositions parent) {
		this(x, 0, z, parent);
	}
	
	public MapPositions getParent() {
		return parent;
	}
	
	public void setParent(MapPositions parent) {
		this.parent = parent;
	}

	public MapPosition(byte[] data, MapPositions parent) {
		this.parent = parent;
		byte type = data[0];
		int pos = 1;
		if ((type & 0x01) == 0x01) {
			this.x = ByteUtilsBE.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x02) == 0x02) {
			this.y = ByteUtilsBE.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x04) == 0x04) {
			this.z = ByteUtilsBE.readDouble(data, pos);
			pos += 8;
		}
		if ((type & 0x08) == 0x08) {
			short length = ByteUtilsBE.readShort(data, pos);
			pos += 2;
			this.name = new String(ByteUtilsBE.substring(data, pos, length));
		}
		if ((type & 0x10) == 0x10) {
			this.a = ByteUtilsBE.readDouble(data, pos);
			pos += 8;
		}
	}

	public byte[] toBytes() throws IOException {
		byte type = 0;
		type = (byte) (type | (x != 0 ? 0x01 : 0x00));
		type = (byte) (type | (y != 0 ? 0x02 : 0x00));
		type = (byte) (type | (z != 0 ? 0x04 : 0x00));
		type = (byte) (type | (name != null ? 0x08 : 0x00));
		type = (byte) (type | (a != 0 ? 0x10 : 0x00));
		System.out.println("Type: " + type);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bytes.write(new byte[] { type });
		if (x != 0)
			bytes.write(ByteUtilsBE.doubleToBytes(x));
		if (y != 0)
			bytes.write(ByteUtilsBE.doubleToBytes(y));
		if (z != 0)
			bytes.write(ByteUtilsBE.doubleToBytes(z));
		if (name != null) {
			bytes.write(ByteUtilsBE.shortToBytes((short) name.length()));
			bytes.write(name.getBytes());
		}
		if (a != 0)
			bytes.write(ByteUtilsBE.doubleToBytes(a));
		return bytes.toByteArray();
	}

	public String toDayZReadableFormatPlayerSpawn() {
		return "<pos x=\"" + this.x + "\" z=\"" + this.z + "\" />";
	}

	public String toDayZReadableFormatEventSpawn() {
		return "<pos x=\"" + this.x + (this.y != 0 ? "\" y=\"" + this.y : "") + "\" z=\"" + this.z + (this.a != 0 ? "\" a=\"" + this.a : "")  + "\" />";
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