package de.lbmaster.dayz.playerdb;

import java.util.ArrayList;
import java.util.List;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class Item {

	private int itemsInside;
	private float health = -1;
	private int stacksize = -1;
	private int contentCount = -1;
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
		this.data = data.getData();
		System.out.println("Data from " + name + " at " + slotX + "," + slotY + ": " + ByteUtilsBE.bytesToHex(this.data.getContent()));
		processItemSpecificData(this.data);
		System.out.println("Item Health: " + this.health);
		int childCount = data.getInt();
		for (int i = 0; i < childCount; i++) {
			Data childData = data.getData();
			Item child = new Item();
			child.loadFromData(childData);
			children.add(child);
		}
	}

	private void processItemSpecificData(Data data) {
		data.skipBytes(1);
		this.uniqueID = data.getInt();
		int tags = data.getInt();
		Data healthData = data.getData();
		System.out.println("HealthData: " + ByteUtilsBE.bytesToHex(healthData.getContent()));
		if (healthData.getLength() == 5) {
			healthData.skipBytes(1);
			this.health = healthData.getFloat();
		}
		Data otherData = data.getData();
		DataArray array = new DataArray(otherData);
		this.stacksize = (int) array.get(1);
		if (!data.isAtEnd()) {
			this.contentCount = Integer.parseInt(data.getNumber());
			System.out.println("Remaining: " + data.getRemainingBytes().length + " ContentCount: " + this.contentCount);
			if (this.contentCount == 0 && data.getRemainingBytes().length > 5) {
				data.rewind(1);
				this.contentCount = 128+data.getByte();
			}
			System.out.println("ContentCount " + contentCount);
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
		return (this.getStackSize() > 1 ? this.getStackSize() + "x " : "") + name + (this.getContentInside() >= 0 ? " (" + this.getContentInside() + ")" : "");
	}

	public float getHealth() {
		return this.health;
	}

	public int getStackSize() {
		return this.stacksize <= 0 ? 1 : this.stacksize;
	}

	public int getContentInside() {
		return this.contentCount;
	}
}
