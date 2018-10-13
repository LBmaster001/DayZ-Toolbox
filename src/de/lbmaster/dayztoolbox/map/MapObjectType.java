package de.lbmaster.dayztoolbox.map;

public enum MapObjectType {
	
	MAP_IMAGE((short) 0), MAP_POINTS((short) 1), UNKNOWN_ID((short)-1);
	
	private short id;
	
	private MapObjectType(short id) {
		this.id = id;
	}
	
	public short getID() {
		return id;
	}
	
	public static MapObjectType getTypeById(short id) {
		for (MapObjectType type : MapObjectType.values()) {
			if (type.getID() == id)
				return type;
		}
		return MapObjectType.UNKNOWN_ID;
	}

}
