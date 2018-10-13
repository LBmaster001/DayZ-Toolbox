package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class MapPositionsTree extends JScrollPane implements TreeExpansionListener {

	private static final long serialVersionUID = 1L;

	private MapFile mapFile;
	private List<MapPositions> positions;
	private JTree jtree;
	private MapJPanel mapRenderer;
	private CustomJTreeRenderer treeRenderer;

	public MapPositionsTree(MapFile mapFile) {
		super();
		this.mapFile = mapFile;
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (!MapPositionsTree.this.mapFile.hasFullyReadContent()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				initPositions();
			}
		}).start();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						keyDeletePress();
					} else if (e.getKeyCode() == KeyEvent.VK_F5) {
						if (mapRenderer != null) 
							mapRenderer.clearPositionsDraw();
						
						initPositions();
					}
				}
				return false;
			}
		});
	}

	public void setMapRenderer(MapJPanel mapJPanel) {
		this.mapRenderer = mapJPanel;
	}

	private void initPositions() {
		System.out.println("InitPositions");
		this.positions = mapFile.getAllPositions();
		DefaultMutableTreeNode mainRoot = new DefaultMutableTreeNode("ROOT");
		for (MapPositions pos : positions) {
			String name = pos.getDisplayName();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(name);
			boolean add = false;
			for (MapPosition position : pos.getPositions()) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(position.toString());
				root.add(child);
				add = true;
			}
			if (add)
				mainRoot.add(root);
		}
		if (jtree != null)
			this.remove(jtree);
		jtree = new JTree(mainRoot);
		jtree.addTreeExpansionListener(this);
		jtree.setRootVisible(false);
		jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jtree.setCellRenderer(treeRenderer = new CustomJTreeRenderer(loadIcon("parent.png"), loadIcon("leaf.png")));
		setViewportView(jtree);
		revalidate();
	}

	private Icon loadIcon(String name) {
		URL resource = MapPositionsTree.class.getResource(name);
		Icon icon = new ImageIcon(resource);
		return icon;
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		String positionsName = event.getPath().toString().substring(7);
		positionsName = positionsName.substring(0, positionsName.length() - 1);
		MapPositions pos = mapFile.getPositionsByDisplayName(positionsName);
		if (pos == null)
			return;
		System.out.println("Found pos.");
		if (mapRenderer != null)
			mapRenderer.removePositionsDraw(pos);
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		String positionsName = event.getPath().toString().substring(7);
		positionsName = positionsName.substring(0, positionsName.length() - 1);
		System.out.println("Name: " + positionsName);
		MapPositions pos = mapFile.getPositionsByDisplayName(positionsName);
		if (pos == null)
			return;
		System.out.println("Found pos.");
		if (mapRenderer != null)
			mapRenderer.addPositionsDraw(pos);
	}

	private void keyDeletePress() {
		System.out.println("Key Typed");
		if (treeRenderer != null) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
			if (selectedNode == null)
				return;
			System.out.println("Found selected Node " + selectedNode.getChildCount());
			if (treeRenderer.isLastSelectionParent()) {
				System.out.println("MapPositions");
				// Is a MapPositionS type
				String positionsName = selectedNode.getPath()[1].toString();
				MapPositions pos = mapRenderer.getMapFile().getPositionsByDisplayName(positionsName);
				if (pos == null)
					return;
				mapRenderer.getMapFile().removeMapObject(pos);
				TreeNode root = selectedNode.getParent();
				System.out.println(root.getChildCount());
				selectedNode.removeFromParent();
				System.out.println(root.getChildCount());
				((DefaultTreeModel) jtree.getModel()).reload();
				if (mapRenderer != null)
					mapRenderer.removePositionsDraw(pos);
			} else {
				// Is a single MapPosition
				System.out.println("single MapPosition");
				System.out.println("PosName: " + selectedNode.getPath()[2].toString());
				String positionsName = selectedNode.getPath()[1].toString();
				MapPositions pos = mapRenderer.getMapFile().getPositionsByDisplayName(positionsName);
				System.out.println("#" + positionsName + "#");
				if (pos == null)
					return;
				positionsName = selectedNode.getPath()[2].toString().trim();
				System.out.println("#" + positionsName + "#");
				pos.removePosition(positionsName);
				TreeNode root = selectedNode.getParent();
				System.out.println(root.getChildCount());
				selectedNode.removeFromParent();
				System.out.println(root.getChildCount());
				((DefaultTreeModel) jtree.getModel()).reload(root);
				mapRenderer.onlyRepaint();
			}
		}
	}

}

class CustomJTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private Icon parent, leaf;

	private DefaultMutableTreeNode lastSelection;
	private boolean lastSelectionParent = false;

	public CustomJTreeRenderer(Icon parent, Icon leaf) {
		this.parent = parent;
		this.leaf = leaf;
	}

	public DefaultMutableTreeNode getLastSelection() {
		return lastSelection;
	}

	public boolean isLastSelectionParent() {
		return lastSelectionParent;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
		if (selected) {
			lastSelection = nodo;
			lastSelectionParent = nodo.getChildCount() > 0 || expanded;
		}
		if (nodo.getChildCount() > 0 && !expanded) {
			setIcon(parent);
		} else {
			setIcon(this.leaf);
		}
		return this;
	}
}
