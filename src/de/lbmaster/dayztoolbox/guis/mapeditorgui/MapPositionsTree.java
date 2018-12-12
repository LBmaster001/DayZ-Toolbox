package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class MapPositionsTree extends JScrollPane implements TreeExpansionListener {

	private static final long serialVersionUID = 1L;

	private MapFile mapFile;
	private List<MapPositions> positions;
	private JTree jtree;
	private MapJPanel mapRenderer;

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

	public void refreshTree() {
		if (jtree != null)
			((DefaultTreeModel) jtree.getModel()).reload();
	}

	public void initPositions() {
		System.out.println("InitPositions");
		this.positions = mapFile.getAllPositions();
		System.out.println("Positionscount: " + positions.size());
		CustomTreeNode mainRoot = new CustomTreeNode("ROOT");
		for (MapPositions pos : positions) {
			String name = pos.getDisplayName();
			CustomTreeNode root = new CustomTreeNode(pos, name);
			boolean add = false;
			for (MapPosition position : pos.getPositions()) {
				System.out.println(position.toString());
				CustomTreeNode child = new CustomTreeNode(position, position.toString());
				root.add(child);
				add = true;
			}
			System.out.println("Adding Positions " + pos.getName() + " ? " + add);
			if (add)
				mainRoot.add(root);
		}
		if (jtree != null)
			this.remove(jtree);
		jtree = new JTree(mainRoot);
		jtree.addTreeExpansionListener(this);
		jtree.setRootVisible(false);
		// jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jtree.setCellRenderer(new CustomJTreeRenderer(loadIcon("parent.png"), loadIcon("leaf.png")));
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

	public void addPosition(MapPosition position, MapJPanel mappanel) {
		if (jtree == null)
			return;
		int[] selectedNodes = jtree.getSelectionRows();

		Map<CustomTreeNode, Boolean> selectedNodesCopy = new HashMap<CustomTreeNode, Boolean>();
		for (int i : selectedNodes) {
			TreeNode node = ((TreeNode) jtree.getPathForRow(i).getLastPathComponent());
			System.out.println(node.toString() + " Parent: " + node.getParent().toString());
			selectedNodesCopy.put((CustomTreeNode) node, node.getParent() != null && node.getParent().toString().equals("ROOT"));
		}
		if (selectedNodesCopy.size() == 0) {
			System.out.println("No nodes Selected");
			openCreateCategoryDialog(position, mappanel);
			return;
		}
		CustomTreeNode selectedNode = null;
		for (Entry<CustomTreeNode, Boolean> entry : selectedNodesCopy.entrySet()) {
			if (entry.getKey() != null) {
				selectedNode = entry.getKey();
				break;
			}
		}
		if (selectedNode == null) {
			System.out.println("No node found !");
			return;
		}
		System.out.println("Found selected Node " + selectedNode.getChildCount());
		String positionsName = selectedNode.getPath()[1].toString();
		MapPositions pos = mapRenderer.getMapFile().getPositionsByDisplayName(positionsName);
		if (pos == null)
			return;
		pos.addPosition(position);
		CustomTreeNode parent = selectedNode;
		if (!selectedNodesCopy.get(selectedNode)) {
			parent = ((CustomTreeNode) selectedNode.getParent());
		}
		parent.add(new CustomTreeNode(position, position.toString()));
		((DefaultTreeModel) jtree.getModel()).reload();
		TreePath path = new TreePath(selectedNode.getPath());
		jtree.expandPath(path);
		jtree.addSelectionPath(path);

		mapRenderer.onlyRepaint();

	}

	private void openCreateCategoryDialog(MapPosition pos, MapJPanel mappanel) {
		MapCreateCategoryGui createGui = new MapCreateCategoryGui(mappanel, pos);
		createGui.setVisible(true);
		createGui.requestFocus();
	}

	private void keyDeletePress() {
		System.out.println("Key Typed");
		int[] selectedNodes = jtree.getSelectionRows();

		List<CustomTreeNode> selectedNodesCopy = new ArrayList<CustomTreeNode>();
		for (int i : selectedNodes) {
			TreeNode node = ((TreeNode) jtree.getPathForRow(i).getLastPathComponent());
			System.out.println(node.toString() + " Parent: " + node.getParent().toString());
			selectedNodesCopy.add((CustomTreeNode) node);
		}

		// selectedNodesCopy.putAll(selectedNodes);
		for (CustomTreeNode entry : selectedNodesCopy) {
			if (entry == null)
				continue;
			System.out.println("Found selected Node " + entry.getChildCount());
			if (entry.isPositions()) {
				System.out.println("MapPositions");
				// Is a MapPositionS type
				MapPositions pos = entry.getMapPositions();
				if (pos == null)
					continue;
				mapRenderer.getMapFile().removeMapObject(pos);
				TreeNode root = entry.getParent();
				System.out.println(root.getChildCount());
				entry.removeFromParent();
				System.out.println(root.getChildCount());
				((DefaultTreeModel) jtree.getModel()).reload();
				if (mapRenderer != null)
					mapRenderer.removePositionsDraw(pos);
			} else if (entry.isPosition()) {
				// Is a single MapPosition
				System.out.println("single MapPosition");
				System.out.println("PosName: " + entry.getMapPosition().getName());
				MapPositions pos = entry.getMapPosition().getParent();
				if (pos == null)
					continue;
				pos.removePosition(entry.getMapPosition());
				TreeNode root = entry.getParent();
				System.out.println(root.getChildCount());
				entry.removeFromParent();
				System.out.println(root.getChildCount());
				((DefaultTreeModel) jtree.getModel()).reload(root);
			} else {
				System.out.println("Node is no Position or Positions ??");
			}
		}
		// selectedNodes.clear();
		mapRenderer.onlyRepaint();
	}
}

class CustomJTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private Icon parent, leaf;

	public CustomJTreeRenderer(Icon parent, Icon leaf) {
		this.parent = parent;
		this.leaf = leaf;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		CustomTreeNode nodo = (CustomTreeNode) value;
		if (nodo.getChildCount() > 0 && !expanded) {
			setIcon(parent);
		} else {
			setIcon(this.leaf);
		}
		return this;
	}
}
