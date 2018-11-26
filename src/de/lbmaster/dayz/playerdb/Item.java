package de.lbmaster.dayz.playerdb;

import java.util.ArrayList;
import java.util.List;

public class Item {

	private int itemsInside;
	private float health = -1;
	private int stacksize = -1;
	private byte slotX;
	private byte slotY;
	private byte sizeX = 1;
	private byte sizeY = 1;
	private int uniqueID = -1;
	private String name, bodyslot;
	private List<Item> children = new ArrayList<Item>();
	private Item parent;

	private Data data;
	
	public Item() {

	}
	
	public Item(String name) {
		this.name = name;
	}

	public Item(String name, int itemsinside) {
		this(name);
		this.itemsInside = itemsinside;
	}

	public void loadFromData(Data data) {
		data.skipBytes(1);
		name = data.findNullTerminatedString();
		slotY = data.getByte();
		slotX = data.getByte();
		bodyslot = data.getString();
		System.out.println("Slot: #" + bodyslot + "#");
		this.data = data.getData();
		int childCount = data.getInt();
		for (int i = 0; i < childCount; i++) {
			Data childData = data.getData();
			Item child = new Item();
			child.loadFromData(childData);
			children.add(child);
		}
	}
	
	private void processItemSpecificData(Data data) {
		this.uniqueID = data.getInt();
		Data healthData = data.getData();
		if (healthData.getLength() == 5) {
			data.skipBytes(1);
			this.health = data.getFloat();
		}
		Data otherData = data.getData();
		otherData.skipToByte((byte) 0xDF);
		otherData.skipBytes(1);
		if (!data.isAtEnd()) {
			Data moreData = new Data(data.getRemainingBytes());
		}
	}
	
	public String getName() {
		return name;
	}

	public void addChild(Item child) {
		this.children.add(child);
		child.parent = this;
	}

	public List<Item> getChildren() {
		return children;
	}

	public int getCurrentChildCount() {
		return children.size();
	}

	public int getSeekedForChildCount() {
		return itemsInside;
	}

	public Item getParent() {
		return parent;
	}

	public void setItemsInside(int itemsinside) {
		this.itemsInside = itemsinside;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
