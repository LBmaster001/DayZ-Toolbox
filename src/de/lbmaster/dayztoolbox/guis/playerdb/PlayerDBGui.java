package de.lbmaster.dayztoolbox.guis.playerdb;

import java.awt.Choice;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayz.playerdb.DBPlayer;
import de.lbmaster.dayz.playerdb.DataParser;
import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;
import de.lbmaster.dayztoolbox.guis.mapeditorgui.MapEditorGui;
import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapPositions;
import de.lbmaster.dayztoolbox.utils.Config;

public class PlayerDBGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextField textField_dblocation;
	private JLabel lblDbid, lblCharid, lblBiUid, lblPosition;
	private JLabel lblBlood;
	private JLabel lblHealth;
	private JLabel lblStats;
	private JTable table_1;
	private JTextField textField_blood;
	private JTextField textField_health;
	private JTextField textField_water;
	private JTextField textField_energy;
	private JLabel lblX;
	private JLabel lblY;
	private JLabel lblZ;
	private JTextField textField_x;
	private JTextField textField_y;
	private JTextField textField_z;
	private JLabel lblHealth_1;
	private Choice choice;
	
	private PlayerInventoryTree invTree;

	static {
		loadDriver();
	}

	public PlayerDBGui(String title) {
		super(title);
		setBounds(150, 130, 950, 800);
		getContentPane().setLayout(
				new FormLayout(new ColumnSpec[] { ColumnSpec.decode("100px"), ColumnSpec.decode("300px:grow"), ColumnSpec.decode("100px"), ColumnSpec.decode("75px"), }, new RowSpec[] { RowSpec.decode("50px"), RowSpec.decode("100px:grow"), RowSpec.decode("25px"), RowSpec.decode("400px"), }));

		JLabel lblPlayerDbEditor = new JLabel("Player DB Editor");
		lblPlayerDbEditor.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerDbEditor.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		getContentPane().add(lblPlayerDbEditor, "1, 1, 4, 1");

		table = new JTable(new CustomTableModel(new Object[] { "DBID", "Online", "Alive", "UID", "SteamID", "SteamName" }));
		table.getSelectionModel().addListSelectionListener(new CustomSelectionModel(this));
		table.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName() + " " + table.getSelectedColumn());
			}
		});
		table.getColumnModel().getColumn(0).setMaxWidth(70);
		table.getColumnModel().getColumn(0).setMinWidth(70);
		table.getColumnModel().getColumn(1).setMaxWidth(60);
		table.getColumnModel().getColumn(1).setMinWidth(60);
		table.getColumnModel().getColumn(2).setMaxWidth(60);
		table.getColumnModel().getColumn(2).setMinWidth(60);
		table.getColumnModel().getColumn(3).setMaxWidth(320);
		table.getColumnModel().getColumn(3).setMinWidth(320);
		table.getColumnModel().getColumn(4).setMaxWidth(160);
		table.getColumnModel().getColumn(4).setMinWidth(160);

		getContentPane().add(new JScrollPane(table), "1, 2, 4, 1, fill, fill");

		JLabel lblDbLocation = new JLabel("DB Location:");
		lblDbLocation.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblDbLocation, "1, 3, right, default");

		textField_dblocation = new JTextField();
		textField_dblocation.setText(Config.getConfig().getString("lastplayerdblocation", ""));
		getContentPane().add(textField_dblocation, "2, 3, fill, default");
		textField_dblocation.setColumns(10);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Browse...");
				chooser.setBounds(252, 41, 50, 19);
				String lastFile = textField_dblocation.getText();
				chooser.setCurrentDirectory(new File(lastFile));

				int returnVal = chooser.showOpenDialog(PlayerDBGui.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File folder = chooser.getSelectedFile();
					lastFile = folder.getAbsolutePath().replace("\\", "/");
					textField_dblocation.setText(lastFile);
				}
			}
		});
		getContentPane().add(btnBrowse, "3, 3");

		JButton btnLoad = new JButton("Load");
		getContentPane().add(btnLoad, "4, 3");
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String dblocation = textField_dblocation.getText();
				File db = new File(dblocation);
				if (!db.exists() || !db.isFile()) {
					ErrorDialog.displayError("DB File does no exsist !");
					return;
				}
				Config.getConfig().setString("lastplayerdblocation", db.getAbsolutePath());
				try {
					Connection con = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
					PreparedStatement stmt = con.prepareStatement("SELECT * FROM Players WHERE 1");
					ResultSet data = stmt.executeQuery();
					JsonObject jsonRoot = new JsonObject();
					JsonArray jsonArr = new JsonArray();
					jsonRoot.add("Entries", jsonArr);
					while (data.next()) {
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						model.addRow(new Object[] { data.getInt("Id"), (data.getInt("Lock") == 1), (data.getInt("Alive") == 1), data.getString("UID") });
						JsonObject obj = new JsonObject();
						obj.addProperty("Id", data.getInt("Id"));
						obj.addProperty("Lock", data.getInt("Lock"));
						obj.addProperty("Alive", data.getInt("Alive"));
						obj.addProperty("UID", data.getString("UID"));
						obj.addProperty("Data", Base64.getEncoder().encodeToString(data.getBytes("Data")));
						jsonArr.add(obj);
					}
//					System.err.println("Player DB:\n" + jsonRoot.toString());
				} catch (SQLException e1) {
					e1.printStackTrace();
					ErrorDialog.displayError("Failed to load Data from DB! " + e1.getMessage());
				}

			}
		});

		JPanel panel = new JPanel();
		getContentPane().add(panel, "1, 4, 4, 1, fill, fill");
		panel.setLayout(null);

		lblDbid = new JLabel("DBID:");
		lblDbid.setBounds(10, 11, 80, 14);
		panel.add(lblDbid);

		lblCharid = new JLabel("CharID:");
		lblCharid.setBounds(100, 11, 90, 14);
		panel.add(lblCharid);

		lblBiUid = new JLabel("BI UID:");
		lblBiUid.setBounds(200, 11, 400, 14);
		panel.add(lblBiUid);

		lblPosition = new JLabel("Position:");
		lblPosition.setBounds(10, 33, 80, 14);
		panel.add(lblPosition);

		lblBlood = new JLabel("Blood:");
		lblBlood.setBounds(100, 64, 42, 14);
		panel.add(lblBlood);

		lblHealth = new JLabel("Health:");
		lblHealth.setBounds(207, 64, 49, 14);
		panel.add(lblHealth);

		lblStats = new JLabel("Stats:");
		lblStats.setBounds(743, 11, 46, 14);
		panel.add(lblStats);

		table_1 = new JTable(new DefaultTableModel(new Object[] { "Stat", "Value" }, 0));
		table_1.setBounds(743, 36, 181, 73);
		panel.add(table_1);

		JLabel lblWater = new JLabel("Water:");
		lblWater.setBounds(321, 64, 42, 14);
		panel.add(lblWater);

		JLabel lblEnergy = new JLabel("Energy:");
		lblEnergy.setBounds(428, 64, 49, 14);
		panel.add(lblEnergy);

		textField_blood = new JTextField();
		textField_blood.setBounds(142, 61, 60, 20);
		panel.add(textField_blood);
		textField_blood.setColumns(10);

		textField_health = new JTextField();
		textField_health.setColumns(10);
		textField_health.setBounds(256, 61, 60, 20);
		panel.add(textField_health);

		textField_water = new JTextField();
		textField_water.setColumns(10);
		textField_water.setBounds(363, 61, 60, 20);
		panel.add(textField_water);

		textField_energy = new JTextField();
		textField_energy.setColumns(10);
		textField_energy.setBounds(477, 61, 60, 20);
		panel.add(textField_energy);

		lblX = new JLabel("X:");
		lblX.setHorizontalAlignment(SwingConstants.RIGHT);
		lblX.setBounds(100, 33, 20, 14);
		panel.add(lblX);

		lblY = new JLabel("Y:");
		lblY.setHorizontalAlignment(SwingConstants.RIGHT);
		lblY.setBounds(210, 33, 20, 14);
		panel.add(lblY);

		lblZ = new JLabel("Z:");
		lblZ.setHorizontalAlignment(SwingConstants.RIGHT);
		lblZ.setBounds(320, 33, 20, 14);
		panel.add(lblZ);

		textField_x = new JTextField();
		textField_x.setColumns(10);
		textField_x.setBounds(130, 30, 80, 20);
		panel.add(textField_x);

		textField_y = new JTextField();
		textField_y.setColumns(10);
		textField_y.setBounds(240, 30, 80, 20);
		panel.add(textField_y);

		textField_z = new JTextField();
		textField_z.setColumns(10);
		textField_z.setBounds(350, 30, 80, 20);
		panel.add(textField_z);

		lblHealth_1 = new JLabel("Health:");
		lblHealth_1.setBounds(10, 64, 80, 14);
		panel.add(lblHealth_1);

		JLabel lblPlayermodel = new JLabel("Playermodel:");
		lblPlayermodel.setBounds(10, 95, 80, 14);
		panel.add(lblPlayermodel);

		choice = new Choice();
		addItemsToChoice(choice);
		choice.setBounds(100, 92, 216, 20);
		panel.add(choice);

		invTree = new PlayerInventoryTree(null);
		invTree.setBounds(10, 120, 306, 269);
		panel.add(invTree);
		
		JButton btnDrawAllPositions = new JButton("Draw All Positions on map");
		btnDrawAllPositions.setBounds(448, 29, 181, 23);
		panel.add(btnDrawAllPositions);
		btnDrawAllPositions.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String mapFile = Config.getConfig().getString(Constants.CONFIG_lastMapFile, null);
				if (mapFile == null) {
					ErrorDialog.displayError("No MapFile found ! Goto MapEditor and set a MapFile Path");
					return;
				} else {
					MapFile mf = new MapFile(mapFile);
					try {
						if (mf.hasImages()) {
							String dblocation = textField_dblocation.getText();
							File db = new File(dblocation);
							Connection con = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
							PreparedStatement stmt = con.prepareStatement("SELECT Data, UID FROM Players WHERE 1");
							Map<String, byte[]> data = new HashMap<String, byte[]>();
							ResultSet rs = stmt.executeQuery();
							while (rs.next()) {
								data.put(rs.getString("UID"), rs.getBytes("Data"));
							}
							List<DBPlayer> players = new DataParser().loadAllDBPlayers(data);
							MapPositions positions = DBPlayer.createMapPositionsFromDBPlayers(players);
							positions.setName("Player Positions");
							mf.addMapObject(positions);
							MapEditorGui gui = new MapEditorGui("Player Positions");
							gui.setVisible(true);
							gui.requestFocus();
							gui.load(mf, true);
							
						} else {
							ErrorDialog.displayError("No Images found in MapFile " + mapFile + " !");
							return;
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						ErrorDialog.displayError("Failed to read MapFile !");
					} catch (SQLException e1) {
						e1.printStackTrace();
						ErrorDialog.displayError("Failed to read DB File !");
					}
				}
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ErrorDialog.displayInfo("This Element is sill very WIP. Expect bugs and not full functionality");
			}
		}).start();
	}
	
	/*
	 * SurvivorF_Frida SurvivorF_Gabi SurvivorF_Helga SurvivorF_Irena
	 * SurvivorF_Judy SurvivorF_Keiko SurvivorF_Lina SurvivorF_Maria
	 * SurvivorF_Naomi SurvivorM_Mirek SurvivorM_Boris SurvivorM_Cyril
	 * SurvivorM_Denis SurvivorM_Elias SurvivorM_Francis SurvivorM_Guo
	 * SurvivorM_Hassan SurvivorM_Indar SurvivorM_Jose SurvivorM_Kaito
	 * SurvivorM_Lewis SurvivorM_Manua SurvivorM_Niki SurvivorM_Oliver
	 * SurvivorM_Peter SurvivorM_Quinn SurvivorM_Rolf SurvivorM_Seth
	 * SurvivorM_Taiki
	 */

	private static String[] models = new String[] { "SurvivorF_Frida", "SurvivorF_Gabi", "SurvivorF_Helga", "SurvivorF_Irena", "SurvivorF_Judy", "SurvivorF_Keiko", "SurvivorF_Lina", "SurvivorF_Maria", "SurvivorF_Naomi", "SurvivorM_Mirek", "SurvivorM_Boris", "SurvivorM_Cyril", "SurvivorM_Denis",
			"SurvivorM_Elias", "SurvivorM_Francis", "SurvivorM_Guo", "SurvivorM_Hassan", "SurvivorM_Indar", "SurvivorM_Jose", "SurvivorM_Kaito", "SurvivorM_Lewis", "SurvivorM_Manua", "SurvivorM_Niki", "SurvivorM_Oliver", "SurvivorM_Peter", "SurvivorM_Quinn", "SurvivorM_Rolf", "SurvivorM_Seth",
			"SurvivorM_Taiki" };

	private void addItemsToChoice(Choice choice) {
		for (String s : models) {
			choice.add(s);
		}
	}

	private static boolean loadDriver() {
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Successfuly loaded Driver");
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void loadRow() {
		int row = table.getSelectedRow();
		int dbid = (int) table.getModel().getValueAt(row, 0);
		String uid = (String) table.getModel().getValueAt(row, 3);
		System.out.println(uid + "  " + dbid);
		lblBiUid.setText("BI UID: " + uid);
		lblDbid.setText("DBID: " + dbid);
		String dblocation = textField_dblocation.getText();
		File db = new File(dblocation);
		try {
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
			PreparedStatement stmt = con.prepareStatement("SELECT Data FROM Players WHERE UID=?");
			stmt.setString(1, uid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				byte[] playerData = rs.getBytes("Data");
				DBPlayer player = new DataParser().loadDBPlayer(playerData, uid);
				lblCharid.setText("CharID: " + player.getCharid());
				textField_x.setText("" + player.getX());
				textField_y.setText("" + player.getY());
				textField_z.setText("" + player.getZ());
				DefaultTableModel model = (DefaultTableModel) table_1.getModel();
				model.getDataVector().clear();
				for (Entry<String, Float> entry : player.getStats().entrySet()) {
					model.addRow(new Object[] { entry.getKey(), entry.getValue() });
				}
				textField_blood.setText("" + player.getBlood());
				textField_health.setText("" + player.getHealth());
				textField_energy.setText("" + player.getEnergy());
				textField_water.setText("" + player.getWater());

				choice.select(player.getModel());
				
				invTree.init(player);
				invTree.expandAllNodes();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

class CustomTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

}

class CustomTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public CustomTableModel(Object[] obj) {
		super(obj, 0);

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}

class CustomSelectionModel implements ListSelectionListener {

	private PlayerDBGui gui;

	public CustomSelectionModel(PlayerDBGui gui) {
		this.gui = gui;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			System.out.println("Change");
			gui.loadRow();
		}
	}

}