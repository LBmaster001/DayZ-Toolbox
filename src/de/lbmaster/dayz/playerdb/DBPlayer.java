package de.lbmaster.dayz.playerdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class DBPlayer {
	
	private float x,y,z;
	private float yaw, pitch, roll;
	private float health, blood, energy, water;
	private Item root = new Item("ROOT");
	private String id, model;
	private Map<String, Float> stats = new HashMap<String, Float>();
	private int charid, dbid;
	
	public DBPlayer(String id) {
		this.id = id;
	}
	
	public static MapPositions createMapPositionsFromDBPlayers(List<DBPlayer> players) {
		MapPositions positions = new MapPositions();
		for (DBPlayer player : players) {
			positions.addPosition(player.createMapPosition());
		}
		return positions;
	}
	
	public MapPosition createMapPosition() {
		MapPosition pos = new MapPosition(getX(), getZ(), null);
		pos.setName(id);
		return pos;
	}

	public Item getInventoryRoot() {
		return root;
	}
	
	public void addItemToRoot(Item item) {
		root.addChild(item);
	}

	public String getID() {
		return id;
	}
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void addStats(Map<String, Float> stats) {
		this.stats.putAll(stats);
	}
	
	public void setIds(int charid, int dbid) {
		this.charid = charid;
		this.dbid = dbid;
	}
	
	public float getEnergy() {
		return energy;
	}

	public void setEnergy(float energy) {
		this.energy = energy;
	}

	public float getWater() {
		return water;
	}

	public void setWater(float water) {
		this.water = water;
	}


	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getBlood() {
		return blood;
	}

	public void setBlood(float blood) {
		this.blood = blood;
	}

	public Item getRoot() {
		return root;
	}

	public void setRoot(Item root) {
		this.root = root;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Float> getStats() {
		return stats;
	}

	public void setStats(Map<String, Float> stats) {
		this.stats = stats;
	}

	public int getCharid() {
		return charid;
	}

	public void setCharid(int charid) {
		this.charid = charid;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}
