package de.lbmaster.dayztoolbox.guis.servermanager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayz.configparser.BattleyeConfig;
import de.lbmaster.dayz.configparser.DayZConfig;
import de.lbmaster.dayz.configparser.DefaultBattleyeConfig;
import de.lbmaster.dayz.configparser.DefaultDayZConfig;
import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;
import de.lbmaster.dayztoolbox.guis.serverconfiggui.ServerConfigGui;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;
import de.lbmaster.dayztoolbox.utils.process.WindowsProcess;
import jbep.dayz.rcon.protocol.BEProtocol;

public class ServerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private String configLocation;
	private DayZConfig serverDZ;
	private Config serverCfg;
	private DayZConfig dzcfg;
	private JLabel lblServerName;

	private Process process = null;
	private boolean processStopRequested = false;
	private JButton btnStart, btnCreateBackup, btnStop, btnLoadBackup, btnDeletePersistance, btnDeleteVehicles, btnJoin, btnSettings, btnDeletePlayers, btnDeleteItemsAnd, btndelete, btnOpenConfig;

	public static Config createNewConfig(String config, String serverConfigName, ServerCreatorGui serverCreatorGui) {
		Config cfg = new Config(PathFinder.findDayZToolBoxFolder() + "/serverconfigs/" + config, false);
		String serverCfgLocation = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER) + "/" + serverConfigName;
		cfg.setString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION, serverCfgLocation);
		cfg.setBoolean(Constants.CONFIG_SERVERMANAGER_UPNP, serverCreatorGui.useUPnP());
		cfg.setBoolean(Constants.CONFIG_SERVERMANAGER_RCON, serverCreatorGui.useRcon());
		cfg.setInt(Constants.CONFIG_SERVERMANAGER_PORT, serverCreatorGui.getPort());
		cfg.write();

		if (serverCreatorGui.useRcon()) {
			try {
				BattleyeConfig becfg = PathFinder.findActiveBEConfig(cfg);
				if (becfg == null) {
					becfg = new DefaultBattleyeConfig(new File(PathFinder.findBEConfigPath(cfg)));
					becfg.setString("RConPassword", serverCreatorGui.getRconPass());
					becfg.save();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return cfg;
	}

	public ServerPanel(String config, final ServerManagerGui mgrGui) {
		this(new Config(PathFinder.findDayZToolBoxFolder() + "/serverconfigs/" + config, false), mgrGui);
	}

	public ServerPanel(final Config serverConfig, final ServerManagerGui mgrGui) {
		this.serverCfg = serverConfig;
		if (serverConfig == null) {
			return;
		} else if (!serverCfg.isRead()) {
			this.serverCfg.read();
		}
		if (!serverConfig.exsists()) {
			return;
		}
		if (!serverConfig.isRead()) {
			serverConfig.read();
		}
		if (!serverConfig.isRead()) {
			System.err.println("Failed to read config " + serverConfig.getFileLocation());
			return;
		}
		try {
			String s = createFromServerConfig(serverConfig);
			if (s != null) {
				System.err.println("Error while loading Config ! " + s + " " + serverConfig.getFileLocation());
				ErrorDialog.displayError("Error while loading Config ! " + s + " " + serverConfig.getFileLocation());
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("100px"), ColumnSpec.decode("190px"), ColumnSpec.decode("170px"), ColumnSpec.decode("190px") }, new RowSpec[] { RowSpec.decode("25px"), RowSpec.decode("30px"), RowSpec.decode("30px"), RowSpec.decode("30px"), }));

		String serverConfigPath = serverConfig.getString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION);
		dzcfg = null;
		try {
			dzcfg = getConfig(serverConfig, serverConfigPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverDZ = dzcfg;

		lblServerName = new JLabel("<html><u>" + (dzcfg != null ? dzcfg.getString("hostname", false) : "Server Name") + "</u></html>");
		add(lblServerName, "1, 1, 4, 1");

		btnStart = new JButton("Start");
		btnStart.setBackground(Color.GREEN);
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startServer();
			}
		});
		add(btnStart, "1, 2");

		btnCreateBackup = new JButton("Create Manual Backup");
		btnCreateBackup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String backup = createFullBackup(false);
				if (backup != null) {
					ErrorDialog.displayInfo("Full Backup Successfully created at " + backup);
				}
			}
		});
		btnCreateBackup.setBackground(Color.CYAN);
		add(btnCreateBackup, "2, 2");

		btnStop = new JButton("Kill/Stop");
		btnStop.setBackground(Color.RED);
		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopServer();
			}
		});
		add(btnStop, "1, 3");

		btnLoadBackup = new JButton("Load Backup");
		btnLoadBackup.setBackground(Color.CYAN);
		add(btnLoadBackup, "2, 3");
		btnLoadBackup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openLoadBackupDialog();
			}
		});

		btnDeletePersistance = new JButton("Delete Persistence");
		btnDeletePersistance.setActionCommand("players,vehicles,loot,events");
		btnDeletePersistance.setBackground(Color.ORANGE);
		add(btnDeletePersistance, "3, 2");

		btnDeleteVehicles = new JButton("Delete Vehicles");
		btnDeleteVehicles.setActionCommand("vehicles");
		btnDeleteVehicles.setBackground(Color.ORANGE);
		add(btnDeleteVehicles, "4, 2");

		btnJoin = new JButton("Join");
		btnJoin.setBackground(Color.GREEN);
		btnJoin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				joinServer();
			}
		});
		add(btnJoin, "1, 4");

		btnSettings = new JButton("Settings");
		btnSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ServerManagerSettingsGui(serverCfg, ServerPanel.this).setVisible(true);
			}
		});
		add(btnSettings, "2, 4");

		btnDeletePlayers = new JButton("Delete Players");
		btnDeletePlayers.setActionCommand("players");
		btnDeletePlayers.setBackground(Color.ORANGE);
		add(btnDeletePlayers, "3, 3");

		btnDeleteItemsAnd = new JButton("Delete Items and Bases");
		btnDeleteItemsAnd.setActionCommand("loot");
		btnDeleteItemsAnd.setBackground(Color.ORANGE);
		add(btnDeleteItemsAnd, "4, 3");

		btndelete = new JButton("Delete Server");
		btndelete.setBackground(Color.RED);
		btndelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serverConfig.delete() && mgrGui != null) {
					mgrGui.removePanel(serverConfig.getFileLocation());
				}
			}
		});

		add(btndelete, "4, 4");

		btnOpenConfig = new JButton("Edit Config");
		btnOpenConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Config.getConfig().setString(Constants.CONFIG_LAST_SERVER_CONFIG_LOADED, new File(serverDZ.getFileLocation()).getName());
				System.out.println("Last Config set to " + Config.getConfig().getString(Constants.CONFIG_LAST_SERVER_CONFIG_LOADED, null));
				new ServerConfigGui("Edit Config").setVisible(true);
			}
		});
		add(btnOpenConfig, "3, 4");

		this.configLocation = serverConfig.getFileLocation();

		ActionListener persistanceListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd == null)
					return;
				boolean players = cmd.contains("players");
				boolean loot = cmd.contains("loot");
				boolean vehicles = cmd.contains("vehicles");
				boolean events = cmd.contains("events");
				deletePersistanceFiles(players, loot, vehicles, events);
			}
		};
		btnDeleteItemsAnd.addActionListener(persistanceListener);
		btnDeletePlayers.addActionListener(persistanceListener);
		btnDeleteVehicles.addActionListener(persistanceListener);
		btnDeletePersistance.addActionListener(persistanceListener);

		startServerWatcherThread();
		onServerStateChange(new ServerEvent(ServerEventType.EVENT_PROCESS_END));
		if (!available(serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 1000))) {
			onServerStateChange(new ServerEvent(ServerEventType.EVENT_RESUME));
		}
	}

	private boolean lastState_rconReachable = false;
	private boolean lastState_isRunning = false;
	private boolean isScheduledShutdown = false;

	private void startServerWatcherThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					boolean rconReachable = false;
					if (serverCfg != null) {
						int port = serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, -1);
						BEProtocol prot = null;
						try {
							BattleyeConfig cfg = PathFinder.findActiveBEConfig(serverCfg);
							if (port != -1 && cfg != null) {

								prot = new BEProtocol("localhost", port);
								prot.setTimeout(1000);
								prot.connect();
								String pw = "...................................";
								prot.login(pw);
								prot.cmd("exit");
								prot.receive();
								prot.disconnect();
								rconReachable = true;
							} else {
								if (port == -1)
									System.out.println("Port not found !");
							}
						} catch (SocketException e) {
							if (prot != null)
								prot.disconnect();
						} catch (UnknownHostException e) {
							if (prot != null)
								prot.disconnect();
							e.printStackTrace();
							System.out.println("Error2");
						} catch (IOException e) {
							if (prot != null)
								prot.disconnect();
						}
					} else {
						System.out.println("No Config Set !");
					}
					if (rconReachable != lastState_rconReachable) {
						lastState_rconReachable = rconReachable;
						if (rconReachable) {
							onServerStateChange(new ServerEvent(ServerEventType.EVENT_RCON_REACHABLE));
						} else {
							onServerStateChange(new ServerEvent(ServerEventType.EVENT_RCON_UNREACHABLE));
						}
					}

					if (process != null) {
						boolean alive = process.isAlive();
						if (alive != lastState_isRunning) {
							lastState_isRunning = alive;
							if (!alive) {
								if (isScheduledShutdown) {
									onServerStateChange(new ServerEvent(ServerEventType.EVENT_PROCESS_END));
								} else {
									onServerStateChange(new ServerEvent(ServerEventType.EVENT_CHRASH));
								}
							}
						}
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void openLoadBackupDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Browse...");
		chooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				if (!f.getName().endsWith(".zip"))
					return false;

				String add = "";
				if (serverCfg != null && serverCfg.getFile() != null) {
					String cfgName = serverCfg.getFile().getName();
					add = cfgName.substring(0, cfgName.indexOf("_") + 1);
				}
				if (!f.getName().startsWith(add))
					return false;
				return true;
			}

			@Override
			public String getDescription() {
				return "Backup Files";
			}

		});
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setBounds(252, 41, 50, 19);
		String backupFolderRaw = serverCfg.getString(Constants.CONFIG_SERVERMANAGER_BACKUPFOLDER);
		String backupFolder = convertBackupFolder(backupFolderRaw);
		chooser.setCurrentDirectory(new File(backupFolder));
		Action details = chooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			String backup = folder.getAbsolutePath().replace("\\", "/");
			System.out.println(backup);
			loadBackup(new File(backup));
		}
	}

	private void onServerStateChange(ServerEvent event) {
		if (event == null)
			return;
		System.out.println("Event " + event.getType().toString());
		switch (event.getType()) {
		case EVENT_START_BY_USER:
			// case EVENT_RCON_REACHABLE:
		case EVENT_RESUME:
			btnStart.setEnabled(false);
			btnJoin.setEnabled(true);
			btnStop.setEnabled(true);
			btnDeleteItemsAnd.setEnabled(false);
			btnDeletePersistance.setEnabled(false);
			btnDeletePlayers.setEnabled(false);
			btnDeleteVehicles.setEnabled(false);
			btnCreateBackup.setEnabled(false);
			btnLoadBackup.setEnabled(false);
			break;
		case EVENT_RCON_UNREACHABLE:
		case EVENT_CHRASH:
		case EVENT_STOP_FORCE_BY_USER:
		case EVENT_PROCESS_END:
		case EVENT_STOP_WINDOWS_REQUEST:
			btnStart.setEnabled(true);
			btnJoin.setEnabled(false);
			btnStop.setEnabled(false);
			btnDeleteItemsAnd.setEnabled(true);
			btnDeletePersistance.setEnabled(true);
			btnDeletePlayers.setEnabled(true);
			btnDeleteVehicles.setEnabled(true);
			btnCreateBackup.setEnabled(true);
			btnLoadBackup.setEnabled(true);
			break;

		default:
			break;
		}
	}

	public Config getServerCfg() {
		return this.serverCfg;
	}

	public DayZConfig getServerDZ() {
		return this.serverDZ;
	}

	private boolean loadBackup(File backup) {
		if (backup == null || !backup.getName().endsWith(".zip"))
			return false;
		return true;
	}

	public String createFullBackup(boolean auto) {
		System.out.println("Creating Full Backup ...");
		String backupFolderRaw = serverCfg.getString(Constants.CONFIG_SERVERMANAGER_BACKUPFOLDER);
		String backupFolder = convertBackupFolder(backupFolderRaw);
		String add = "";
		if (serverCfg != null && serverCfg.getFile() != null) {
			String cfgName = serverCfg.getFile().getName();
			add = cfgName.substring(0, cfgName.indexOf("_") + 1);
		}
		File backupFile = new File(backupFolder + "/" + add + getDate() + (auto ? "_auto" : "_manual") + ".zip");
		ZipOutputStream out = null;
		try {
			File parent = backupFile.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
			if (!backupFile.exists())
				backupFile.createNewFile();
			out = new ZipOutputStream(new FileOutputStream(backupFile));

			{ // Persistence Files
				String lastServerDir = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
				String mpMission = this.serverDZ.getString("Missions.DayZ.template", false);
				int instanceID = this.serverDZ.getInteger("instanceId", false);
				String persistanceFolder = lastServerDir + "/mpmissions/" + mpMission + "/storage_" + instanceID;
				File playerdb = new File(persistanceFolder + "/players.db");
				addZipEntry(out, "persistence/players.db", playerdb);

				File data = new File(persistanceFolder + "/data");
				if (data.exists() && data.isDirectory()) {
					for (File f : data.listFiles()) {
						addZipEntry(out, "persistence/data/" + f.getName(), f);
					}
				}
			}
			{ // Mission
				String lastServerDir = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
				String mpMission = this.serverDZ.getString("Missions.DayZ.template", false);
				String missionFolder = lastServerDir + "/mpmissions/" + mpMission;
				addMissionZipEntrys(out, "mission", new File(missionFolder));
			}
			{ // Configs
				serverCfg.write();
				File cfg = serverCfg.getFile();
				addZipEntry(out, "configs/" + cfg.getName(), cfg);
				try {
					serverDZ.save();
				} catch (Exception e) {
				}
				File cfgDZ = serverDZ.getFile();
				addZipEntry(out, "configs/" + cfgDZ.getName(), cfgDZ);

				if (serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_RCON)) {
					BattleyeConfig rconCfg = PathFinder.findActiveBEConfig(serverCfg);
					if (rconCfg != null) {
						addZipEntry(out, "configs/" + rconCfg.getFile().getName(), rconCfg.getFile());
					}
				}
			}
			out.close();
			System.out.println("Full Backup created successfully");
			return backupFile.getAbsolutePath();

		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog.displayError("Failed to create Full Backup! " + e.getMessage());
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	private void addMissionZipEntrys(ZipOutputStream out, String zipLocation, File file) {
		if (file == null)
			return;
		if (file.getName().startsWith("storage_"))
			return;
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				addMissionZipEntrys(out, zipLocation + "/" + file.getName(), child);
			}
		} else {
			int attempt = 0;
			int waittime = 40;
			while (attempt < 10) {
				try {
					attempt++;
					addZipEntry(out, zipLocation + "/" + file.getName(), file);
					break;
				} catch (IOException e) {
					System.out.println("Zip Entry creation failed ! Retry No. " + attempt + " Waiting " + waittime + "ms for next try");
					try {
						Thread.sleep(waittime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private void addZipEntry(ZipOutputStream out, String zipLocation, File file) throws IOException {
		ZipEntry entry = new ZipEntry(zipLocation);
		out.putNextEntry(entry);

		int attempt = 0;
		int waittime = 200;
		byte[] data = null;
		while (attempt < 10) {
			try {
				attempt++;
				data = Files.readAllBytes(file.toPath());
				break;
			} catch (IOException e) {
				System.out.println("Zip Entry creation failed ! Retry No. " + attempt + " Waiting " + waittime + "ms for next try");
				try {
					Thread.sleep(waittime);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (data != null)
			out.write(data, 0, data.length);
		out.closeEntry();
	}

	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return format.format(new Date(System.currentTimeMillis()));
	}

	public void onFocusGained() {
		if (dzcfg != null)
			try {
				dzcfg.read();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		lblServerName.setText("<html><u>" + (dzcfg != null ? dzcfg.getString("hostname", false) : "Server Name") + "</u></html>");
	}

	public void onSettingsChanged() {

	}

	public String getConfigLocation() {
		return this.configLocation;
	}

	private String createFromServerConfig(Config cfg) throws IOException {
		if (cfg == null)
			return "Internal Error";
		if (!cfg.isRead())
			cfg.read();
		String serverConfigPath = cfg.getString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION);
		if (serverConfigPath == null || serverConfigPath.length() == 0)
			return "No Server Config Path Set !";

		DayZConfig dzcfg = getConfig(cfg, serverConfigPath);
		if (dzcfg != null) {
			return null;
		}
		return "serverDZ.cfg not found at " + serverConfigPath;
	}

	private DayZConfig getConfig(Config cfg, String serverConfigPath) throws IOException {
		DayZConfig dzcfg = null;
		if (new File(serverConfigPath).exists()) {
			dzcfg = new DayZConfig(serverConfigPath);
			dzcfg.read();
		} else {
			dzcfg = new DefaultDayZConfig(serverConfigPath);
		}
		if (dzcfg != null && dzcfg.isRead()) {
			createDefaultServerConfig(cfg, dzcfg);
			dzcfg.saveIgnore();
			return dzcfg;
		}
		return null;
	}

	private void createDefaultServerConfig(Config cfg, DayZConfig serverDZConfigLocation) {
		cfg.setString(Constants.CONFIG_SERVERMANAGER_SERVERDZLOCATION, serverDZConfigLocation.getFileLocation());
		cfg.setDefaultBoolean(Constants.CONFIG_SERVERMANAGER_AUTOBACKUP, true);
		cfg.setDefaultString(Constants.CONFIG_SERVERMANAGER_BACKUPFOLDER, "{serverfolder}/backups");
		cfg.setDefaultInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302);
		cfg.write();
	}

	public static String convertBackupFolder(String file) {
		String s = file.replace("{serverfolder}", Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER));
		s = s.replace("{toolboxfolder}", PathFinder.findDayZToolBoxFolder());
		return s;
	}

	private long lastStopRequest = 0;

	public void stopServer() {
		boolean shutdownWithRcon = false;
		serverCfg.read();
		if (serverCfg != null && serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_RCON, false)) {
			System.out.println("Trying to shutdown Server via Rcon");
			try {
				BattleyeConfig cfg = PathFinder.findActiveBEConfig(serverCfg);
				if (cfg != null) {
					shutdownWithRcon = true;
					BEProtocol prot = new BEProtocol("localhost", serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT));
					prot.setDiscardMessages(false);
					prot.setTimeout(1000);
					prot.connect();
					prot.login(cfg.getString("RConPassword", ""));
					prot.cmd("#shutdown");
					prot.cmd("unknfmdsfjusdnf");
					prot.disconnect();
					System.out.println("Server was shutdown via Rcon");
					onServerStateChange(new ServerEvent(ServerEventType.EVENT_STOPREQUEST_RCON_BY_USER));
					isScheduledShutdown = true;
				} else {
					System.out.println("No BEConfig found !");
				}
			} catch (SocketException | UnknownHostException e) {
				System.out.println("Failed to shutdown server via Rcon " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Error while Sending shutdown command " + e.getMessage());
			}
		}
		WindowsProcess p = WindowsProcess.findDayZServerProcess(serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT));
		if (p != null) {
			p.endProcess();
			onServerStateChange(new ServerEvent(ServerEventType.EVENT_STOP_WINDOWS_REQUEST));
			isScheduledShutdown = true;
		} else if (process != null) {
			if (process.isAlive()) {
				if (processStopRequested) {
					isScheduledShutdown = true;
					onServerStateChange(new ServerEvent(ServerEventType.EVENT_STOP_FORCE_BY_USER));
					process.destroyForcibly();
					System.out.println("Trying to destroy Server Process Forcibly");
				} else if (!shutdownWithRcon) {
					isScheduledShutdown = true;
					onServerStateChange(new ServerEvent(ServerEventType.EVENT_STOPREQUEST_BY_USER));
					process.destroy();
					System.out.println("Trying to destroy Server Process");
				}
			}
			if (serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_UPNP, false)) {
				closeUPNPPorts(serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302));
			}
		}
		long time = System.currentTimeMillis();
		if (lastStopRequest + 1000 > time) {
			processStopRequested = true;
		} else {
			processStopRequested = false;
		}
		lastStopRequest = time;
	}

	public void startServer() {
		isScheduledShutdown = false;

		serverCfg.read();
		if (serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_AUTOBACKUP)) {
			createFullBackup(true);
		}
		if (process != null) {
			if (process.isAlive()) {
				ErrorDialog.displayInfo("Server still running ! Press Stop to stop it");
				return;
			}
		}
		processStopRequested = false;
		String serverExePath = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER) + "/dayzserver_x64.exe";
		String configPath = this.serverDZ.getFileLocation();
		int port = this.serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302);
		List<String> params = new ArrayList<String>();
		params.add(new File(serverExePath).getAbsolutePath());
		params.add("-scrAllowFileWrite");
		params.add("-port=" + port);
		if (!available(port)) {
			ErrorDialog.displayError("There is a Server already running on port " + port + " you might want to change it in the settings");
			return;
		}

		params.add(buildModsParameter(true));

		List<String> parameters = new ArrayList<String>();
		String extraParams = this.serverCfg.getString(Constants.CONFIG_SERVERMANAGER_PARAMETERS);
		if (extraParams != null)
			for (String s : extraParams.split(" -")) {
				parameters.add(s);
			}
		parameters.add("-config=" + configPath);
		if (serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_RCON)) {
			System.out.println("Rcon is enabled");
			String name = new File(serverCfg.getFileLocation()).getName();
			name = name.substring(0, name.lastIndexOf("."));
			parameters.add("-bepath=" + name);
			parameters.add("-profiles=profiles");
		}
		if (parameters != null) {
			for (String param : parameters) {
				params.add(param.startsWith("-") ? param : "-" + param);
			}
		}
		String[] startParams = new String[params.size()];
		for (int i = 0; i < startParams.length; i++) {
			startParams[i] = params.get(i);
		}
		try {
			loadKeys();
			ProcessBuilder pb = new ProcessBuilder(startParams);
			pb.directory(new File(serverExePath).getParentFile());
			this.process = pb.start();
			onServerStateChange(new ServerEvent(ServerEventType.EVENT_START_BY_USER));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (serverCfg.getBoolean(Constants.CONFIG_SERVERMANAGER_UPNP, false)) {
			openUPNPPorts(serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302));
		}
	}

	private String buildModsParameter(boolean isServer) {
		List<String> loadedMods;
		if (isServer) {
			loadedMods = this.serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		} else {
			loadedMods = this.serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_CLIENT);
		}
		StringBuilder modParam = new StringBuilder();
		boolean add = loadedMods != null && !loadedMods.isEmpty();
		if (add) {
			modParam.append("\"-mod=");
		}
		for (String modPath : loadedMods) {

			modParam.append(modPath + ";");
		}
		if (add) {
			modParam.append("\"");
		}
		return modParam.toString();
	}

	private void loadKeys() {
		List<String> loadedMods = this.serverCfg.getList(Constants.CONFIG_SERVERMANAGER_LIST_MODS_LOADED_SERVER);
		String serverPath = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);

		File keysFolder = new File(serverPath + "/Keys");
		if (!keysFolder.exists())
			keysFolder.mkdirs();
		if (!keysFolder.isDirectory()) {
			return;
		}
		File[] keysInstalled = keysFolder.listFiles();
		if (keysInstalled != null) {
			for (File f : keysInstalled) {
				if (!f.getName().startsWith("dayz"))
					f.delete();
			}
		}
		for (String modFolder : loadedMods) {
			File keysFolderMod = new File(modFolder + "/keys");
			System.out.println(keysFolderMod.getAbsolutePath());
			if (keysFolderMod.exists() && keysFolderMod.isDirectory()) {
				File[] modKeys = keysFolderMod.listFiles();
				System.out.println("Keys to install: " + modKeys.length);
				for (File modKey : modKeys) {
					File modKeyCopyLocation = new File(keysFolder.getAbsoluteFile() + "/" + modKey.getName());
					if (modKey.isFile() && !modKeyCopyLocation.exists()) {
						try {
							Files.copy(modKey.toPath(), modKeyCopyLocation.toPath());
							System.out.println("Intalled Key " + modKey.getName());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void joinServer() {
		String hostname = "localhost";
		int port = 2302;
		if (this.serverCfg != null) {
			port = this.serverCfg.getInt(Constants.CONFIG_SERVERMANAGER_PORT, 2302);
		}
		if (!available(port)) {
			System.out.println("Port is in use");
		} else {
			System.out.println("Port not used");
			ErrorDialog.displayInfo("It looks like there is no Server running on Port " + port);
			return;
		}
		String pass = "";
		List<String> params = new ArrayList<String>();
		if (this.serverDZ != null) {
			pass = this.serverDZ.getString("password", false);
		}

		params.add("-connect=" + hostname);
		params.add("-port=" + port);
		params.add((pass.length() > 0 ? "-password=" + pass + "" : ""));
		params.add(buildModsParameter(false));

		String serverPath = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
		try {
			String version = PathFinder.findServer64BitVersion(serverPath);
			if (version != null && version.length() > 0) {
				String clientPath = PathFinder.getFittingClientPath(version);

				String[] startParams = new String[params.size() + 2];
				startParams[0] = new File(PathFinder.getFittingClientPath(version)).getParent() + "/dayz_be.exe";
				startParams[1] = "-exe DayZ_x64.exe";
				for (int i = 2; i < startParams.length; i++) {
					startParams[i] = params.get(i - 2);
				}

				if (clientPath != null && clientPath.length() > 0) {
					// Found Client with the same version as the server
					ProcessBuilder pb = new ProcessBuilder(startParams);
					pb.directory(new File(clientPath).getParentFile());
					pb.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean available(int port) {
		if (port < 1000 || port > 65535) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	private void deletePersistanceFiles(boolean players, boolean loot, boolean vehicles, boolean events) {
		try {
			this.serverDZ.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String lastServerDir = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
		String mpMission = this.serverDZ.getString("Missions.DayZ.template", false);
		int instanceID = this.serverDZ.getInteger("instanceId", false);
		String persistanceFolder = lastServerDir + "/mpmissions/" + mpMission + "/storage_" + instanceID;
		List<File> files = listToDeletePersistanceFiles(persistanceFolder, players, loot, vehicles, events);
		new ServerManagerDeleteConfirmation(files, this).setVisible(true);
	}

	private static List<File> listToDeletePersistanceFiles(String persistanceFolder, boolean players, boolean loot, boolean vehicles, boolean events) {
		List<File> delete = new ArrayList<File>();
		File persistanceDir = new File(persistanceFolder);
		if (!persistanceDir.exists() || !persistanceDir.getName().toLowerCase().startsWith("storage")) {
			return delete;
		}
		File dbFile = new File(persistanceDir.getAbsolutePath() + "/players.db");
		File vehiclesFile = new File(persistanceDir.getAbsolutePath() + "/data/vehicles.bin");
		File eventsFile = new File(persistanceDir.getAbsolutePath() + "/data/events.bin");
		File dataFolder = new File(persistanceDir.getAbsolutePath() + "/data");
		if (players && dbFile.exists()) {
			delete.add(dbFile);
		}
		if (vehicles && vehiclesFile.exists()) {
			delete.add(vehiclesFile);
		}
		if (events && eventsFile.exists()) {
			delete.add(eventsFile);
		}
		if (loot) {
			if (!dataFolder.exists() || !dataFolder.isDirectory())
				return delete;
			boolean ok = true;
			for (File dynamic : dataFolder.listFiles()) {
				if (dynamic.getName().startsWith("dynamic")) {
					delete.add(dynamic);
				}
			}
			if (!ok)
				return delete;
		}
		return delete;
	}

	private void openUPNPPorts(int basePort) {
		int[] ports = new int[] { basePort, basePort + 2, basePort + 24714 };

		try {
			System.out.println("Looking for UPnP gateway device...");
			GatewayDiscover discover = new GatewayDiscover();
			Map<InetAddress, GatewayDevice> gatewayMap = discover.discover();
			if (gatewayMap == null || gatewayMap.isEmpty()) {
				System.out.println("There are no UPnP gateway devices");
				ErrorDialog.displayError("Failed to open ports via UPnP !");
			} else {
				for (Entry<InetAddress, GatewayDevice> addr : gatewayMap.entrySet()) {
					System.out.println("UPnP gateway device found on " + addr.getKey().getHostAddress());
				}
				GatewayDevice gateway = discover.getValidGateway();
				if (gateway == null) {
					System.out.println("There is no connected UPnP gateway device");
					ErrorDialog.displayError("Failed to open ports via UPnP !");
				} else {
					InetAddress localAddress = gateway.getLocalAddress();
					InetAddress externalAddress = InetAddress.getByName(gateway.getExternalIPAddress());
					System.out.println("Using UPnP gateway device on " + localAddress.getHostAddress());
					System.out.println("External IP address is " + externalAddress.getHostAddress());
					for (int port : ports) {
						gateway.addPortMapping(port, port, localAddress.getHostAddress(), "UDP", "DayZ Server");
					}
				}
			}
		} catch (Exception exc) {
			System.out.println("Unable to discover UPnP gateway devices: " + exc.toString());
			ErrorDialog.displayError("Failed to open ports via UPnP !");
		}
	}

	private void closeUPNPPorts(int basePort) {
		int[] ports = new int[] { basePort, basePort + 2, basePort + 24714 };

		try {
			System.out.println("Looking for UPnP gateway device...");
			GatewayDiscover discover = new GatewayDiscover();
			Map<InetAddress, GatewayDevice> gatewayMap = discover.discover();
			if (gatewayMap == null || gatewayMap.isEmpty()) {
				System.out.println("There are no UPnP gateway devices");
			} else {
				for (Entry<InetAddress, GatewayDevice> addr : gatewayMap.entrySet()) {
					System.out.println("UPnP gateway device found on " + addr.getKey().getHostAddress());
				}
				GatewayDevice gateway = discover.getValidGateway();
				if (gateway == null) {
					System.out.println("There is no connected UPnP gateway device");
				} else {
					InetAddress localAddress = gateway.getLocalAddress();
					InetAddress externalAddress = InetAddress.getByName(gateway.getExternalIPAddress());
					System.out.println("Using UPnP gateway device on " + localAddress.getHostAddress());
					System.out.println("External IP address is " + externalAddress.getHostAddress());
					for (int port : ports) {
						gateway.deletePortMapping(port, "UDP");
					}
				}
			}
		} catch (Exception exc) {
			System.out.println("Unable to discover UPnP gateway devices: " + exc.toString());
		}
	}
}