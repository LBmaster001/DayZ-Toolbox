package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import javax.swing.tree.DefaultMutableTreeNode;

import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class CustomTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private MapPosition pos;
	private MapPositions positions;

	public CustomTreeNode(MapPosition pos) {
		super();
		this.pos = pos;
	}
	public CustomTreeNode(MapPositions pos) {
		super();
		this.positions = pos;
	}
	public CustomTreeNode(MapPosition pos, Object obj) {
		super(obj);
		this.pos = pos;
	}
	public CustomTreeNode(MapPositions pos, Object obj) {
		super(obj);
		this.positions = pos;
	}
	public CustomTreeNode(Object obj) {
		super(obj);
	}
	
	public boolean isPosition() {
		return pos != null;
	}
	
	public boolean isPositions() {
		return positions != null;
	}
	
	public MapPosition getMapPosition() {
		return pos;
	}
	
	public MapPositions getMapPositions() {
		return positions;
	}

}
