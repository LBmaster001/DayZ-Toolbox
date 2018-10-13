package de.lbmaster.dayztoolbox.map;

import java.io.IOException;

public abstract class MapObject {
	
	protected MapObjectType type;
	
	public MapObject(MapObjectType type) {
		this.type = type;
	}
	
	public MapObjectType getType() {
		return type;
	}
	
	public abstract byte[] toBytes() throws IOException;

}
