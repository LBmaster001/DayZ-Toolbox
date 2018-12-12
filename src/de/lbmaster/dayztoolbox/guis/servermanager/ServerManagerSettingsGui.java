package de.lbmaster.dayztoolbox.guis.servermanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayz.configparser.BattleyeConfig;
import de.lbmaster.dayz.configparser.DefaultBattleyeConfig;
import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;
import de.lbmaster.dayztoolbox.mods.DayZMod;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;
import de.lbmaster.dayztoolbox.utils.UIDGenerator;

public class ServerManagerSettingsGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner textField;
	private JTextField textField_1, textField_2, textField_3, textField_4;
	private JCheckBox chckbxUpnp, chckbxRcon, chckbxAutobackup;
	private JTable table_modsavailable, table_modsloaded;

	private Config serverCfg;

	public ServerManagerSettingsGui(Config serverCfg, final ServerPanel serverPanel) {
		super("Server Settings (" + (new File(serverCfg.getFileLocation()).getName()) + ")");
		this.serverCfg = serverCfg;
		serverCfg.read();
		setBounds(100, 100, 600, 650);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("70px"), ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));

		chckbxUpnp = new JCheckBox("UPnP");
		chckbxUpnp.setSelected(serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_UPNP, false));
		contentPanel.add(chckbxUpnp, "2, 2");

		JLabel lblServerPort = new JLabel("Server Port:");
		contentPanel.add(lblServerPort, "4, 2, right, default");

		textField = new JSpinner();
		textField.setEditor(new JSpinner.NumberEditor(textField, "#"));
		textField.setValue(serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302));
		contentPanel.add(textField, "6, 2, fill, default");

		chckbxRcon = new JCheckBox("Rcon");
		chckbxRcon.setSelected(serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_RCON, false));
		contentPanel.add(chckbxRcon, "2, 4");

		JLabel lblRconPassword = new JLabel("Rcon Password:");
		contentPanel.add(lblRconPassword, "4, 4, right, default");

		textField_1 = new JTextField();
		textField_1.setEditable(chckbxRcon.isSelected());
		try {
			BattleyeConfig cfg = PathFinder.findActiveBEConfig(serverCfg);
			if (cfg != null) {
				textField_1.setText(cfg.getString("RConPassword", UIDGenerator.generateUID(16)));
			} else {
				cfg = new DefaultBattleyeConfig(new File(PathFinder.findBEConfigPath(serverCfg)));
				cfg.save();
				textField_1.setText(cfg.getString("RConPassword", UIDGenerator.generateUID(16)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		contentPanel.add(textField_1, "6, 4, 2, 1, fill, default");
		textField_1.setColumns(10);

		chckbxAutobackup = new JCheckBox("Auto-Backup");
		chckbxAutobackup.setSelected(serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_AUTOBACKUP, false));
		contentPanel.add(chckbxAutobackup, "2, 6");

		JLabel lblBackupFolder = new JLabel("Backup Folder:");
		contentPanel.add(lblBackupFolder, "4, 6, right, default");

		textField_2 = new JTextField();
		textField_2.setText(serverCfg.getString(Constants.CONFIG_SERVERMANAGER_BACKUPFOLDER, "{serverfolder}/backups"));
		contentPanel.add(textField_2, "6, 6, 2, 1, fill, default");
		textField_2.setColumns(10);

		JLabel lblConfig = new JLabel("Config:");
		contentPanel.add(lblConfig, "4, 8, right, default");

		textField_3 = new JTextField();
		textField_3.setText(serverCfg.getString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION));
		contentPanel.add(textField_3, "6, 8, 2, 1, fill, default");
		textField_3.setColumns(10);

		JLabel lblModsLoaded = new JLabel("Mods Loaded");
		lblModsLoaded.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPanel.add(lblModsLoaded, "2, 10, 1, 2");

		JLabel lblStartParameter = new JLabel("Start Parameter:");
		contentPanel.add(lblStartParameter, "4, 10, right, default");

		textField_4 = new JTextField();
		textField_4.setText(serverCfg.getString(Constants.CONFIG_SERVERMANAGER_PARAMETERS, ""));
		contentPanel.add(textField_4, "6, 10, 2, 1, fill, default");
		textField_4.setColumns(10);

		Object[][] data = new Object[][] {};

		table_modsloaded = new JTable(createTableModel(data, false));
		table_modsloaded.getTableHeader().setReorderingAllowed(false);
		table_modsloaded.getColumnModel().getColumn(1).setMaxWidth(55);
		table_modsloaded.getColumnModel().getColumn(2).setMaxWidth(55);
		addUnloadListener(table_modsloaded);
		table_modsloaded.setDefaultRenderer(String.class, new CustomTableRenderer(serverCfg));
		contentPanel.add(new JScrollPane(table_modsloaded), "2, 12, 6, 1, fill, fill");

		JLabel lblModsAvailable = new JLabel("Mods Available");
		lblModsAvailable.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPanel.add(lblModsAvailable, "2, 14");

		JButton btnAddExternalMod = new JButton("Add external Mod");
		contentPanel.add(btnAddExternalMod, "4, 14, 3, 1");
		btnAddExternalMod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openAddModBrowser();
			}
		});

		table_modsavailable = new JTable(createTableModel(data, true));
		table_modsavailable.getTableHeader().setReorderingAllowed(false);
		table_modsavailable.getColumnModel().getColumn(1).setMaxWidth(55);
		table_modsavailable.getColumnModel().getColumn(2).setMaxWidth(55);
		addLoadListener(table_modsavailable);
		table_modsavailable.setDefaultRenderer(String.class, new CustomTableRenderer(serverCfg));
		contentPanel.add(new JScrollPane(table_modsavailable), "2, 16, 6, 1, fill, fill");
		chckbxUpnp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		addWorkshopMods();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("Save");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
				setVisible(false);
				if (serverPanel != null)
					serverPanel.onSettingsChanged();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(getDefaultCloseListener());

		chckbxRcon.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				textField_1.setEditable(chckbxRcon.isSelected());
			}
		});
	}

	private void openAddModBrowser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Browse...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setBounds(252, 41, 50, 19);
		String lastExternalModFolder = serverCfg.getString(Constants.CONFIG_SERVERMANAGER_LAST_EXTERNAL_MOD_FOLDER, null);
		if (lastExternalModFolder == null) {
			lastExternalModFolder = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER, "");
		}
		chooser.setCurrentDirectory(new File(lastExternalModFolder));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			String modlocation = folder.getAbsolutePath().replace("\\", "/");
			System.out.println(modlocation);
			try {
				DayZMod mod = new DayZMod(modlocation);
				if (mod.isMod() && mod.getModRootFolder() != null) {
					List<String> externalmods = Config.getConfig().getList(Constants.CONFIG_LIST_EXTERNAL_MODS);
					String path = mod.getModRootFolder().getAbsolutePath();
					if (!externalmods.contains(path)) {
						externalmods.add(path);
						Config.getConfig().write();
						DefaultTableModel model = (DefaultTableModel) this.table_modsavailable.getModel();
						model.addRow(new Object[] { mod, mod.isServerMod(), mod.isClientMod() });
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addWorkshopMods() {
		if (this.table_modsavailable == null)
			return;
		List<DayZMod> mods = DayZMod.findAllWorkshopMods();
		
		List<String> modsExternal = Config.getConfig().getList(Constants.CONFIG_LIST_EXTERNAL_MODS);
		System.out.println("external mods: " + modsExternal.size());
		for (String modLoc : modsExternal) {
			System.out.println("Adding external Mod: " + modLoc);
			try {
				DayZMod mod = new DayZMod(modLoc);
				if (mod.isMod() && mod.getModRootFolder() != null && mod.getModRootFolder().exists()) {
					mods.add(mod);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		DefaultTableModel model = (DefaultTableModel) this.table_modsavailable.getModel();
		List<String> loadedMods = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		List<String> loadedModsClient = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_CLIENT);
		List<String> allMods = new ArrayList<String>();
		for (DayZMod mod : mods) {
			if (loadedMods == null || !loadedMods.contains(mod.getModRootFolder().getAbsolutePath())) {
				model.addRow(new Object[] { mod, mod.isServerMod(), mod.isClientMod() });
			}
		}
		System.out.println((loadedMods != null) + " " + (loadedModsClient != null));
		if (loadedMods != null && loadedModsClient != null) {
			DefaultTableModel model2 = (DefaultTableModel) this.table_modsloaded.getModel();
			allMods.addAll(loadedMods);
			for (String mod : loadedModsClient) {
				if (!allMods.contains(mod))
					allMods.add(mod);
			}
			for (String modlocation : allMods) {
				try {
					DayZMod mod = new DayZMod(modlocation);
					System.out.println("Loaded mod " + modlocation);
					model2.addRow(new Object[] { mod, loadedMods.contains(modlocation), loadedModsClient.contains(modlocation) });
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadMod(int row) {
		DefaultTableModel model2 = (DefaultTableModel) this.table_modsavailable.getModel();
		@SuppressWarnings("unchecked")
		DayZMod mod = (DayZMod) (((Vector<? extends Object>) model2.getDataVector().get(row)).firstElement());
		model2.removeRow(row);
		DefaultTableModel model = (DefaultTableModel) this.table_modsloaded.getModel();
		model.addRow(new Object[] { mod, mod.isServerMod(), mod.isClientMod() });

		List<String> loadedMods = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		List<String> loadedModsClient = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_CLIENT);
		if (!loadedMods.contains(mod.getModRootFolder().getAbsolutePath())) {
			if (mod.isServerMod())
				loadedMods.add(mod.getModRootFolder().getAbsolutePath());
			if (mod.isClientMod())
				loadedModsClient.add(mod.getModRootFolder().getAbsolutePath());
			boolean dependenciesLoaded = mod.hasAllDepencencies(loadedMods);
			if (!dependenciesLoaded) {
				System.out.println("Need to load Dependencies for mod " + mod.getModName());
				loadDependencies(mod);
			}
		}
	}
	
	private void loadDependencies(DayZMod mod) {
		for (String dependency : mod.getDependencies()) {
			if (dependency == null)
				continue;
			int row = getModRow(DayZMod.getModByFolderName(dependency));
			if (row >= 0) {
				loadMod(row);
			}
		}
	}

	private int getModRow(DayZMod mod2) {
		DefaultTableModel model = (DefaultTableModel) table_modsavailable.getModel();
		for (int i = 0; i < table_modsavailable.getRowCount(); i++) {
			DayZMod mod = (DayZMod) (((Vector<? extends Object>) model.getDataVector().get(i)).firstElement());
			if (mod == mod2)
				return i;
		}
		return -1;
	}
	
	private void unloadMod(int row) {
		DefaultTableModel model2 = (DefaultTableModel) this.table_modsloaded.getModel();
		@SuppressWarnings("unchecked")
		DayZMod mod = (DayZMod) (((Vector<? extends Object>) model2.getDataVector().get(row)).firstElement());
		model2.removeRow(row);
		DefaultTableModel model = (DefaultTableModel) this.table_modsavailable.getModel();
		model.addRow(new Object[] { mod, mod.isServerMod(), mod.isClientMod() });

		List<String> loadedMods = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		List<String> loadedModsClient = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_CLIENT);
		loadedMods.remove(mod.getModRootFolder().getAbsolutePath());
		loadedModsClient.remove(mod.getModRootFolder().getAbsolutePath());
	}

	private void addLoadListener(final JTable table) {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.out.println(" double click");
					loadMod(table.getSelectedRow());
				}
			}
		});
	}

	private void addUnloadListener(final JTable table) {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.out.println(" double click");
					unloadMod(table.getSelectedRow());
				}
			}
		});
	}

	private DefaultTableModel createTableModel(Object[][] data, final boolean blockAll) {
		Object[] columnnames = new Object[] { "Mod Name", "Server", "Client" };
		DefaultTableModel model = new DefaultTableModel(data, columnnames) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int row, int col) {
				Object obj = super.getValueAt(row, col);
				if (obj != null && obj instanceof DayZMod) {
					DayZMod mod = ((DayZMod) obj);
					String name = (mod.isWorkshopMod() ? "(WS) " : "(Ext) ") + mod.getFullModname();
					if (mod.getModRootFolder() == null || !mod.getModRootFolder().exists()) {
						name = "(Not found!) " + name;
					}
					return name;
				}
				return obj;
			}

			@Override
			public Class<? extends Object> getColumnClass(int column) {
				return (column == 1 || column == 2) ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				if (blockAll)
					return false;
				@SuppressWarnings("unchecked")
				DayZMod mod = (DayZMod) (((Vector<? extends Object>) getDataVector().get(row)).firstElement());
				if (column == 1) {
					if (!mod.isServerMod()) {
						ErrorDialog.displayError("This mod can not be used as a server mod !");
					}
					return mod.isServerMod();
				} else if (column == 2) {
					if (!mod.isClientMod()) {
						ErrorDialog.displayError("This mod can not be used as a client mod ! No BiKeys to install found !");
					}
					return mod.isClientMod();
				}
				return column != 0;
			}
		};
		model.addTableModelListener(new CheckBoxModelListener());
		return model;
	}

	public class CheckBoxModelListener implements TableModelListener {

		@SuppressWarnings("unchecked")
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			System.out.println("Change in Row: " + row + " Column: " + column);
			if (column == 1 || column == 2) {
				DefaultTableModel model = (DefaultTableModel) e.getSource();
				String columnName = model.getColumnName(column);
				Boolean checked = (Boolean) model.getValueAt(row, column);
				if (checked) {
					System.out.println(columnName + ": " + true);
				} else {
					System.out.println(columnName + ": " + false);
				}
				DayZMod mod = (DayZMod) (((Vector<? extends Object>) model.getDataVector().get(row)).firstElement());
				if (column == 1) {
					List<String> loadedMods = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
					if (checked)
						loadedMods.add(mod.getModRootFolder().getAbsolutePath());
					else
						loadedMods.remove(mod.getModRootFolder().getAbsolutePath());
				} else if (column == 2) {
					List<String> loadedModsClient = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_CLIENT);
					if (checked)
						loadedModsClient.add(mod.getModRootFolder().getAbsolutePath());
					else
						loadedModsClient.remove(mod.getModRootFolder().getAbsolutePath());
				}
			}
		}
	}

	private void save() {
		serverCfg.setBoolean(Constants.CONFIG_SERVERMANAGER_UPNP, chckbxUpnp.isSelected());
		serverCfg.setInt(Constants.CONFIG_SERVERMANAGER_PORT, Integer.parseInt(textField.getValue() + ""));
		serverCfg.setBoolean(Constants.CONFIG_SERVERMANAGER_RCON, chckbxRcon.isSelected());
		serverCfg.setBoolean(Constants.CONFIG_SERVERMANAGER_AUTOBACKUP, chckbxAutobackup.isSelected());
		serverCfg.setString(Constants.CONFIG_SERVERMANAGER_BACKUPFOLDER, textField_2.getText());
		serverCfg.setString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION, textField_3.getText());
		serverCfg.setString(Constants.CONFIG_SERVERMANAGER_PARAMETERS, textField_4.getText());
		serverCfg.write();

		try {
			BattleyeConfig cfg = PathFinder.findActiveBEConfig(serverCfg);
			if (cfg != null) {
				cfg.setString("RConPassword", textField_1.getText().trim());
				cfg.save();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class CustomTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	Color backgroundColor = getBackground();
	Config serverCfg;
	
	public CustomTableRenderer(Config serverCfg) {
		this.serverCfg = serverCfg;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		@SuppressWarnings("unchecked")
		DayZMod mod = (DayZMod) (((Vector<? extends Object>) model.getDataVector().get(row)).firstElement());
		List<String> loadedMods = serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		System.out.println("Mods loaded: " + loadedMods.size());
		boolean dependenciesLoaded = mod.hasAllDepencencies(loadedMods);
		System.out.println("Dependencies loaded for mod " + mod.getFullModname() + " ? " + dependenciesLoaded);
		if (mod == null || mod.getModRootFolder() == null || !mod.getModRootFolder().exists() || (!dependenciesLoaded && loadedMods.contains(mod.getModRootFolder().getAbsolutePath()))) {
			c.setBackground(new Color(244, 50, 50));
		} else if (!isSelected) {
			c.setBackground(backgroundColor);
		}
		return c;
	}
}