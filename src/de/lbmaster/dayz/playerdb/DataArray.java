package de.lbmaster.dayz.playerdb;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class DataArray {

	private float[][] array;
	
	public DataArray(Data data) {
		System.out.println("Content: " + ByteUtilsBE.bytesToHex(data.getContent()));
		data.skipToByte((byte) 0xDF);
		System.out.println("Content: " + ByteUtilsBE.bytesToHex(data.getRemainingBytes()));
		data.skipBytes(1);
		int size1 = data.getNumber(2);
		System.out.println("ArraySize1 " + size1);
		int size2 = 0;
		if (size1 > 0) {
			data.skipBytes(1);
			size2 = data.getNumber(2);
			System.out.println("ArraySize2 " + size2);
			this.array = new float[size1][size2];
			for (int i = 0; i < size1; i++) {
				for (int j = 0; j < size2; j++) {
					data.skipBytes(3); // Might be risky
					this.array[i][j] = Float.parseFloat(data.getNumber());
					System.out.println("Loaded Value " + this.array[i][j]);
				}
			}
		}
		
	}
    
	public DataArray(int size1, int size2) {
		this.array = new float[size1][size2];
	}
	
	public DataArray(int size) {
		this(1, size);
	}
	
	public float get(int index) {
		return get(0, index);
	}
	
	public float get(int index1, int index2) {
		if (hasValue(index1, index2)) {
			return array[index1][index2];
		}
		return 0.0f;
	}
	
	public void set(int index1, int index2, float value) {
		if (hasValue(index1, index2)) {
			array[index1][index2] = value;
		}
	}
	
	public void set(int index, float value) {
		set(0, index, value);
	}
	
	public boolean hasAnyContent() {
		return array != null && array.length > 0;
	}
	
	public boolean hasValue(int index1, int index2) {
		return array != null && array.length > index1 && array[index1].length > index2;
	}
}
