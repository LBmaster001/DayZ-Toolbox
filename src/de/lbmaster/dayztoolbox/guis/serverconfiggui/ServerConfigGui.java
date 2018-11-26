package de.lbmaster.dayztoolbox.guis.serverconfiggui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.xswingx.JXTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayz.configparser.DayZConfig;
import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;
import de.lbmaster.dayztoolbox.guis.maingui.MainGui;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.NumberFormatterOverride;
import javax.swing.JSplitPane;

public class ServerConfigGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private JTextField textFieldHostName;
	private JXTextField textFieldAdminPassword, textFieldPassword, textFieldStartTime;
	private JTextField textFieldLogFile;
	private JCheckBox boxSaveHouseStates, boxFirstPersonOnly, boxEnableDebugMonitor, boxDisableCroshair, boxVerifyClientFiles, boxStorageAutoFix, boxForceSameVersion, boxVonEnabled, boxLogMemory, boxLogPlayers, boxLogAverageFps, boxSaveTime;
	private JSpinner spinnerInstanceID, spinnerMaxPlayers, spinnerMaxPing, spinnerRespawnTime, spinnerMaxQueuePlayers, spinnerParallelProcessing, spinnerPersistenceFileCount, spinnerVoNQuality, spinnerTimeAcceleration;
	private Choice choiceTimeStampFormat;

	private static final int smallHeight = 370;
	private static final int fullHeight = 593;
	private JLabel lblMissionTemplate;
	private JTextField textFieldMissionTemplate;
	private JSplitPane splitPane;
	private Choice configchoice;

	public ServerConfigGui(String title) {
		super(title);
		boolean advancedOptions = Config.getConfig().getBoolean(Constants.CONFIG_advancedConfigView, false);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(150, 130, 780, advancedOptions ? fullHeight : smallHeight);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("max(100px;default)"), ColumnSpec.decode("max(150px;default):grow"), ColumnSpec.decode("max(100px;default)"), ColumnSpec.decode("max(150px;default):grow"), },
				new RowSpec[] { RowSpec.decode("max(50dlu;default)"), RowSpec.decode("max(30px;default)"), RowSpec.decode("33px"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"),
						RowSpec.decode("max(23px;default):grow"), RowSpec.decode("max(23px;default)"), RowSpec.decode("33px"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"),
						RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), RowSpec.decode("max(23px;default)"), }));

		JLabel lblServerConfigGui = new JLabel("Server Config");
		lblServerConfigGui.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerConfigGui.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		contentPanel.add(lblServerConfigGui, "1, 1, 4, 1");

		JLabel lblConfigLocation = new JLabel("Config:");
		lblConfigLocation.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblConfigLocation, "1, 2, fill, default");

		configchoice = new Choice();
		contentPanel.add(configchoice, "2, 2");
		addConfigChoiceItems();
		int index = getItemIndex(Config.getConfig().getString(Constants.CONFIG_lastConfigLoaded));
		System.out.println("Choice index: " + index + " " + Config.getConfig().getString(Constants.CONFIG_lastConfigLoaded));
		if (index >= 0) {
			configchoice.select(index);
		}
		configchoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				loadFile();
			}
		});

		JLabel lblBasicOptions = new JLabel("Basic Options");
		lblBasicOptions.setVerticalAlignment(SwingConstants.TOP);
		lblBasicOptions.setHorizontalAlignment(SwingConstants.CENTER);
		lblBasicOptions.setFont(new Font(Constants.FONT, Font.BOLD, 18));
		contentPanel.add(lblBasicOptions, "1, 3, 4, 1");

		JLabel lblHostname = new JLabel("Hostname:");
		lblHostname.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblHostname, "1, 4, right, default");

		textFieldHostName = new JTextField();
		contentPanel.add(textFieldHostName, "2, 4, 3, 1, fill, default");
		textFieldHostName.setColumns(10);

		JLabel lblPassword = new JLabel("Server Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblPassword, "1, 5, right, default");

		textFieldPassword = new JXTextField();
		textFieldPassword.setPrompt("Password Disabled");
		contentPanel.add(textFieldPassword, "2, 5, fill, default");
		textFieldPassword.setColumns(10);

		JLabel lblAdminpassword = new JLabel("Admin Password:");
		lblAdminpassword.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAdminpassword, "3, 5, right, default");

		textFieldAdminPassword = new JXTextField();
		textFieldAdminPassword.setPrompt("Password Disabled");
		contentPanel.add(textFieldAdminPassword, "4, 5, fill, default");
		textFieldAdminPassword.setColumns(10);

		JLabel lblMaxPlayers = new JLabel("Max Players:");
		lblMaxPlayers.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMaxPlayers, "1, 6, right, default");

		NumberFormat format = NumberFormat.getInstance();
		NumberFormatterOverride formatter1 = new NumberFormatterOverride(format);
		formatter1.setValueClass(Integer.class);
		formatter1.setMinimum(1);
		formatter1.setMaximum(999);
		formatter1.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter1.setCommitsOnValidEdit(false);

		spinnerMaxPlayers = new JSpinner();
		spinnerMaxPlayers.setModel(new SpinnerNumberModel(50, 1, 9999, 1));
		contentPanel.add(spinnerMaxPlayers, "2, 6, left, default");

		JLabel lblMaxQueuePlayers = new JLabel("Max Queue Players:");
		lblMaxQueuePlayers.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMaxQueuePlayers, "3, 6, right, default");

		NumberFormatterOverride formatter2 = new NumberFormatterOverride(format);
		formatter2.setValueClass(Integer.class);
		formatter2.setMinimum(0);
		formatter2.setMaximum(24);
		formatter2.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead
		// of focus lost
		formatter2.setCommitsOnValidEdit(false);

		spinnerMaxQueuePlayers = new JSpinner();
		spinnerMaxQueuePlayers.setModel(new SpinnerNumberModel(0, 0, 999, 1));
		contentPanel.add(spinnerMaxQueuePlayers, "4, 6, left, default");

		JLabel lblMaxPing = new JLabel("Max Ping:");
		lblMaxPing.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMaxPing, "1, 7, right, default");

		spinnerMaxPing = new JSpinner();
		spinnerMaxPing.setModel(new SpinnerNumberModel(200, 0, 9999, 1));
		contentPanel.add(spinnerMaxPing, "2, 7, left, default");

		JLabel lblRespawnTime = new JLabel("Respawn Time:");
		lblRespawnTime.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblRespawnTime, "3, 7, right, default");

		spinnerRespawnTime = new JSpinner();
		spinnerRespawnTime.setModel(new SpinnerNumberModel(0, 0, 999, 1));
		contentPanel.add(spinnerRespawnTime, "4, 7, left, default");

		boxFirstPersonOnly = new JCheckBox("1st Person only");
		contentPanel.add(boxFirstPersonOnly, "2, 8");

		boxEnableDebugMonitor = new JCheckBox("Enable Debug Monitor");
		contentPanel.add(boxEnableDebugMonitor, "3, 8");

		boxDisableCroshair = new JCheckBox("Disable Crosshair");
		contentPanel.add(boxDisableCroshair, "4, 8");

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		splitPane.setBorder(null);
		splitPane.setSize(splitPane.getWidth(), 23);
		contentPanel.add(splitPane, "4, 9, fill, fill");

		spinnerTimeAcceleration = new JSpinner();
		spinnerTimeAcceleration.setModel(new SpinnerNumberModel(0, 0, 24, 1));
		splitPane.setLeftComponent(spinnerTimeAcceleration);

		boxSaveTime = new JCheckBox("Save Time");
		splitPane.setRightComponent(boxSaveTime);

		JLabel lblStarttime = new JLabel("StartTime:");
		lblStarttime.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblStarttime, "1, 9, right, default");

		textFieldStartTime = new JXTextField();
		textFieldStartTime.setToolTipText("\"SystemTime\" for current Time of the Server System\r\n\"YYYY/MM/DD/HH/MM\" format");
		textFieldStartTime.setPrompt("YYYY/MM/DD/HH/MM");
		contentPanel.add(textFieldStartTime, "2, 9, fill, default");
		textFieldStartTime.setColumns(10);

		JLabel lblTimeAcceleration = new JLabel("Time Acceleration:");
		lblTimeAcceleration.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblTimeAcceleration, "3, 9, right, default");

		JLabel lblAdvancedOptions = new JLabel("Advanced Options");
		lblAdvancedOptions.setVerticalAlignment(SwingConstants.TOP);
		lblAdvancedOptions.setFont(new Font(Constants.FONT, Font.BOLD, 18));
		lblAdvancedOptions.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblAdvancedOptions, "1, 10, 4, 1");

		boxVerifyClientFiles = new JCheckBox("Verify Client Files");
		contentPanel.add(boxVerifyClientFiles, "2, 11");

		boxForceSameVersion = new JCheckBox("Force same Version");
		contentPanel.add(boxForceSameVersion, "3, 11");

		boxStorageAutoFix = new JCheckBox("Storage Auto Fix");
		contentPanel.add(boxStorageAutoFix, "4, 11");

		JLabel lblVonQuality = new JLabel("VoN Quality:");
		lblVonQuality.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblVonQuality, "1, 12, right, default");

		spinnerVoNQuality = new JSpinner();
		spinnerVoNQuality.setModel(new SpinnerNumberModel(0, 0, 30, 1));
		contentPanel.add(spinnerVoNQuality, "2, 12, left, default");

		boxVonEnabled = new JCheckBox("VoN Enabled");
		contentPanel.add(boxVonEnabled, "3, 12");

		JLabel lblInstanceid = new JLabel("InstanceID:");
		lblInstanceid.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblInstanceid, "1, 13, right, default");

		spinnerInstanceID = new JSpinner();
		spinnerInstanceID.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		contentPanel.add(spinnerInstanceID, "2, 13, left, default");

		JLabel lblQueue = new JLabel("Queue parallel processing:");
		contentPanel.add(lblQueue, "3, 13, right, default");

		spinnerParallelProcessing = new JSpinner();
		spinnerParallelProcessing.setModel(new SpinnerNumberModel(1, 1, 99, 1));
		contentPanel.add(spinnerParallelProcessing, "4, 13, left, default");

		boxSaveHouseStates = new JCheckBox("Save House States");
		contentPanel.add(boxSaveHouseStates, "2, 14");

		JLabel lblPersistenceFileCount = new JLabel("Persistence File Count:");
		lblPersistenceFileCount.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblPersistenceFileCount, "3, 14, right, default");

		spinnerPersistenceFileCount = new JSpinner();
		spinnerPersistenceFileCount.setModel(new SpinnerNumberModel(1, 1, 99, 1));
		contentPanel.add(spinnerPersistenceFileCount, "4, 14, left, default");

		JLabel lblTimestampformat = new JLabel("Timestampformat:");
		lblTimestampformat.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblTimestampformat, "1, 15, right, default");

		choiceTimeStampFormat = new Choice();
		choiceTimeStampFormat.add("Short");
		choiceTimeStampFormat.add("Full");
		contentPanel.add(choiceTimeStampFormat, "2, 15");

		boxLogMemory = new JCheckBox("Log Memory ");
		contentPanel.add(boxLogMemory, "2, 16");

		boxLogPlayers = new JCheckBox("Log Players ");
		contentPanel.add(boxLogPlayers, "3, 16");

		boxLogAverageFps = new JCheckBox("Log Average Fps ");
		contentPanel.add(boxLogAverageFps, "4, 16");

		JLabel lblLogFile = new JLabel("Log File:");
		lblLogFile.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLogFile, "1, 17, right, default");

		textFieldLogFile = new JTextField();
		contentPanel.add(textFieldLogFile, "2, 17, 2, 1, fill, default");
		textFieldLogFile.setColumns(10);

		lblMissionTemplate = new JLabel("Mission Template:");
		lblMissionTemplate.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMissionTemplate, "1, 18, right, default");

		textFieldMissionTemplate = new JTextField();
		contentPanel.add(textFieldMissionTemplate, "2, 18, 2, 1, fill, default");
		textFieldMissionTemplate.setColumns(10);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		final JToggleButton btnShowAdvancedOptions = new JToggleButton("show advanced options");
		btnShowAdvancedOptions.setFocusable(false);
		btnShowAdvancedOptions.setSelected(advancedOptions);
		buttonPane.add(btnShowAdvancedOptions);

		btnShowAdvancedOptions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnShowAdvancedOptions.isSelected()) {
					setSize(getWidth(), fullHeight);
				} else {
					setSize(getWidth(), smallHeight);
				}
				Config.getConfig().setBoolean(Constants.CONFIG_advancedConfigView, btnShowAdvancedOptions.isSelected());
			}
		});

		JButton okButton = new JButton("Save");
		buttonPane.add(okButton);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean success = saveValues(new File(Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder) + "/" + configchoice.getSelectedItem()));
				if (!success)
					return;
				close();
				MainGui frame = MainGui.getFrame();
				if (frame != null)
					frame.updateButtons();
			}
		});
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(getDefaultCloseListener());
		loadFile();
	}

	private void addConfigChoiceItems() {
		for (File f : new File(Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder)).listFiles()) {
			if (f.isFile() && f.getName().endsWith(".cfg")) {
				configchoice.add(f.getName());
			}
		}
	}

	private int getItemIndex(String item) {
		for (int i = 0; i < configchoice.getItemCount(); i++) {
			if (configchoice.getItem(i).equals(item))
				return i;
		}
		return -1;
	}

	private boolean saveValues(File file) {
		System.out.println("Saving Values to: " + file.getAbsolutePath());
		DayZConfig cfg = new DayZConfig(file);
		try {
			cfg.read();
			System.out.println("Read Config to prepare for writing");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Set all Values
		cfg.setString("hostname", textFieldHostName.getText());
		cfg.setString("password", textFieldPassword.getText());
		cfg.setString("passwordAdmin", textFieldAdminPassword.getText());
		cfg.setString("serverTime", textFieldStartTime.getText());
		cfg.setString("timeStampFormat", choiceTimeStampFormat.getSelectedItem());
		cfg.setString("logFile", textFieldLogFile.getText());
		cfg.setString("Missions.DayZ.template", textFieldMissionTemplate.getText());

		cfg.setInteger("instanceId", (int) spinnerInstanceID.getValue());
		cfg.setInteger("maxPing", (int) spinnerMaxPing.getValue());
		cfg.setInteger("maxPlayers", (int) spinnerMaxPlayers.getValue());
		cfg.setInteger("loginQueueMaxPlayers", (int) spinnerMaxQueuePlayers.getValue());
		cfg.setInteger("loginQueueConcurrentPlayers", (int) spinnerParallelProcessing.getValue());
		cfg.setInteger("lootHistory", (int) spinnerPersistenceFileCount.getValue());
		cfg.setInteger("respawnTime", (int) spinnerRespawnTime.getValue());
		cfg.setInteger("serverTimeAcceleration", (int) spinnerTimeAcceleration.getValue());
		cfg.setInteger("vonCodecQuality", (int) spinnerVoNQuality.getValue());

		cfg.setInteger("disableCrosshair", boxDisableCroshair.isSelected() ? 1 : 0);
		cfg.setInteger("enableDebugMonitor", boxEnableDebugMonitor.isSelected() ? 1 : 0);
		cfg.setInteger("disable3rdPerson", boxFirstPersonOnly.isSelected() ? 1 : 0);
		cfg.setInteger("forceSameBuild", boxForceSameVersion.isSelected() ? 1 : 0);
		cfg.setBoolean("storeHouseStateDisabled", !boxSaveHouseStates.isSelected());
		cfg.setInteger("serverTimePersistent", boxSaveTime.isSelected() ? 1 : 0);
		cfg.setInteger("storageAutoFix", boxStorageAutoFix.isSelected() ? 1 : 0);
		cfg.setInteger("verifySignatures", boxVerifyClientFiles.isSelected() ? 2 : 0);
		cfg.setInteger("disableVoN", boxVonEnabled.isSelected() ? 0 : 1);

		cfg.setInteger("logMemory", boxLogMemory.isSelected() ? 1 : 0);
		cfg.setInteger("logAverageFps", boxLogAverageFps.isSelected() ? 1 : 0);
		cfg.setInteger("logPlayers", boxLogPlayers.isSelected() ? 1 : 0);

		try {

			if (!cfg.canWrite()) {
				ErrorDialog.displayError("The Config \"" + cfg.getFileLocation() + "\" can not be written ! Maybe the Server blocks the file access ?");
				return false;
			}

			System.out.println("Save successful ? " + cfg.save());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to write config " + cfg.getFileLocation());
			System.out.println("Failed to write config " + cfg.getFileLocation());
		}
		return true;
	}

	private void loadFile() {
		File f = new File(Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder) + "/" + configchoice.getSelectedItem());
		if (!f.exists()) {
			ErrorDialog.displayError("The File \"" + f + "\" does not exsist !");
			return;
		}
		if (!f.canRead()) {
			ErrorDialog.displayError("The File \"" + f + "\" can not be read !");
			return;
		}
		Config.getConfig().setString(Constants.CONFIG_lastConfigLoaded, configchoice.getSelectedItem());
		System.out.println("Loading File " + f.getAbsolutePath());
		DayZConfig cfg = new DayZConfig(f);
		try {
			System.out.println("Config Read: " + cfg.read());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to read config " + cfg.getFileLocation());
			System.out.println("Failed to read config " + cfg.getFileLocation());
		}
		try {

			textFieldHostName.setText(cfg.getString("hostname", "NO HOSTNAME!"));
			textFieldPassword.setText(cfg.getString("password", ""));
			textFieldAdminPassword.setText(cfg.getString("passwordAdmin", ""));
			textFieldStartTime.setText(cfg.getString("serverTime", "SystemTime"));
			choiceTimeStampFormat.select(cfg.getString("timeStampFormat", "Short"));
			textFieldLogFile.setText(cfg.getString("logFile", "server_console.log"));
			textFieldMissionTemplate.setText(cfg.getString("Missions.DayZ.template", "dayzOffline.chernarusplussss"));

			spinnerInstanceID.setValue(cfg.getInteger("instanceId", 0));
			spinnerMaxPing.setValue(cfg.getInteger("maxPing", 200));
			spinnerMaxPlayers.setValue(cfg.getInteger("maxPlayers", 60));
			spinnerMaxQueuePlayers.setValue(cfg.getInteger("loginQueueMaxPlayers", 500));
			spinnerParallelProcessing.setValue(cfg.getInteger("loginQueueConcurrentPlayers", 5));
			spinnerPersistenceFileCount.setValue(cfg.getInteger("lootHistory", 1));
			spinnerRespawnTime.setValue(cfg.getInteger("respawnTime", 5));
			spinnerTimeAcceleration.setValue(cfg.getInteger("serverTimeAcceleration", 0));
			spinnerVoNQuality.setValue(cfg.getInteger("vonCodecQuality", 7));

			boxDisableCroshair.setSelected(cfg.getInteger("disableCrosshair", 0) == 1);
			boxEnableDebugMonitor.setSelected(cfg.getInteger("enableDebugMonitor", 0) == 1);
			boxFirstPersonOnly.setSelected(cfg.getInteger("disable3rdPerson", 0) == 1);
			boxForceSameVersion.setSelected(cfg.getInteger("forceSameBuild", 1) == 1);
			boxSaveHouseStates.setSelected(!cfg.getBoolean("storeHouseStateDisabled", cfg.isDefaultcasesensitive(), false));
			boxSaveTime.setSelected(cfg.getInteger("serverTimePersistent", 0) == 1);
			boxStorageAutoFix.setSelected(cfg.getInteger("storageAutoFix", 1) == 1);
			boxVerifyClientFiles.setSelected(cfg.getInteger("verifySignatures", 2) == 2);
			boxVonEnabled.setSelected(cfg.getInteger("disableVoN", 0) == 0);

			boxLogMemory.setSelected(cfg.getInteger("logMemory", 0) == 1);
			boxLogAverageFps.setSelected(cfg.getInteger("logAverageFps", 0) == 1);
			boxLogPlayers.setSelected(cfg.getInteger("logPlayers", 0) == 1);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.displayError("Failed to read the Config File! Check your config for any syntax error. " + e.getMessage() + "");
		}
		contentPanel.revalidate();
		contentPanel.repaint();
	}
}
