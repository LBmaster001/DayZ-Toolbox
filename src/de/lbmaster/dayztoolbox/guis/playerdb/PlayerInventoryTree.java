package de.lbmaster.dayztoolbox.guis.playerdb;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import de.lbmaster.dayz.playerdb.DBPlayer;
import de.lbmaster.dayz.playerdb.Item;
import de.lbmaster.dayztoolbox.guis.mapeditorgui.MapPositionsTree;

public class PlayerInventoryTree extends JScrollPane implements TreeExpansionListener {
	
	private static final long serialVersionUID = 1L;
	
	private JTree jtree;

	public PlayerInventoryTree(DBPlayer player) {
		init(player);
	}
	
	public void init(DBPlayer player) {
		if (player == null)
			return;
		DefaultMutableTreeNode mainRoot = new DefaultMutableTreeNode("ROOT");
		for (Item rootItem : player.getRoot().getChildren()) {
			addItemToRoot(rootItem, mainRoot);
		}
		loadPlayerInventory(player, mainRoot);
	}
	
	private void addItemToRoot(Item item, DefaultMutableTreeNode root) {
		DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item);
		root.add(itemNode);
		for (Item child : item.getChildren()) {
			addItemToRoot(child, itemNode);
		}
	}
	
	public void loadPlayerInventory(DBPlayer player, DefaultMutableTreeNode mainRoot) {
		if (jtree != null)
			this.remove(jtree);
		jtree = new JTree(mainRoot);
		jtree.addTreeExpansionListener(this);
		jtree.setRootVisible(false);
		// jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		jtree.setCellRenderer(new CustomJTreeRenderer(loadIcon("parent.png"), loadIcon("leaf.png")));
		setViewportView(jtree);
		revalidate();
	}
	
	public void expandAllNodes() {
		expandAllNodes(jtree, 0, jtree.getRowCount());
	}
	
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for(int i=0;i<rowCount;++i){
	        tree.expandRow(i);
	    }

	    if(tree.getRowCount()!=rowCount){
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	    }
	}
	
	private Icon loadIcon(String name) {
		URL resource = MapPositionsTree.class.getResource(name);
		Icon icon = new ImageIcon(resource);
		return icon;
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent e) {
		
	}

	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		
	}

	
}
