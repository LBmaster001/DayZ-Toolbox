package de.lbmaster.dayztoolbox;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import de.lbmaster.dayztoolbox.guis.maingui.MainGui;
import de.lbmaster.dayztoolbox.updater.Updater;
import de.lbmaster.dayztoolbox.updater.Version;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class MainClass {

	public static final int mainVersion = 0;
	public static final int buildVersion = 0;
	public static final int buildId = 11048;

	public static void main(String[] args) {
		setupErrorFile();
		Config.getConfig().setAutoSave(true);

		checkMemory();
		System.out.println("JVM is 64bit ? " + is64BitJVM());
		findDayZServerFolder();
		findArma3ToolsFolder();
		findPBOManagerFolder();
		findDayZClientFolder();
		findPal2PacEFolder();
		// new ErrorReporter();
		doUIManagerStuff();
		setUIFont(new FontUIResource(Constants.FONT, Font.PLAIN, 12));
		MainGui mainGui = new MainGui();
		mainGui.setVisible(true);
		removeOldUpdater();
		boolean updateAvailable = hasUpdateAvailable();
		System.out.println("Is Update Available ? " + updateAvailable);
		if (updateAvailable)
			mainGui.openUpdateDialog(false);
	}
	
	private static void setupErrorFile() {
		try {
			String errorFileLocation = PathFinder.findDayZToolBoxFolder() + "/error.log";
			if (errorFileLocation.startsWith("null")) {
				errorFileLocation = "error.log";
			}
			File errorLog = new File(errorFileLocation);
			if (errorLog.exists())
				errorLog.delete();
			errorLog.createNewFile();
			if (!errorLog.exists()) {
				errorLog.getParentFile().mkdirs();
				errorLog.createNewFile();
			}
			System.setErr(new PrintStream(new FileOutputStream(errorLog)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkMemory() {
		System.out.println((Runtime.getRuntime().maxMemory()/1024/1024) + "mb available");
	}
	
	private static boolean is64BitJVM() {
		System.out.println("Java Version: " + System.getProperty("java.version") + " " + System.getProperty("sun.arch.data.model") + "bit");
		String version = System.getProperty("sun.arch.data.model");
		return version != null && (version.endsWith("64") || !version.contains("32"));
	}

	private static void doUIManagerStuff() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			System.out.println("Look and Feel com.sun.java.swing.plaf.windows.WindowsLookAndFeel was set successfuly !");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private static void removeOldUpdater() {
		File updater = new File("DayZToolboxUpdater.jar");
		if (updater.exists())
			updater.delete();
	}

	public static void update() {
		if (hasUpdateAvailable()) {
			try {
				File updater = new File("DayZToolboxUpdater.jar");
				while (updater.exists() && !updater.delete()) {
					Thread.sleep(500);
				}
				Files.copy(MainClass.class.getResourceAsStream("DayZToolboxUpdater.jar"), updater.toPath());
				if (!updater.exists())
					Thread.sleep(500);
				if (updater.exists()) {
					Runtime.getRuntime().exec("java -jar DayZToolboxUpdater.jar");
					System.exit(0);
					System.out.println("Started Updater");
				} else {
					System.out.println("Copy Failed!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean hasUpdateAvailable() {
		Updater updater = new Updater("toast-teamspeak.de", 80);
		if (updater.loadJson()) {
			Version v = updater.getNewestVersion();
			if (v != null) {
				return !new Version(getBuildString()).isNewest(v);
			} else {
				System.out.println("No Versions found !");
			}
		} else {
			System.out.println("Failed to load Updates !");
		}
		return false;
	}
	
	private static void findPal2PacEFolder() {
		if (Config.getConfig().getString(Constants.CONFIG_pal2pace, null) == null || !PathFinder.validatePal2PacEPath(Config.getConfig().getString(Constants.CONFIG_pal2pace, null))) {
			List<String> found = PathFinder.getPossiblePal2PacELocations();
			if (found.size() > 0) {
				Config.getConfig().setString(Constants.CONFIG_pal2pace, found.get(0).replace("\\", "/"));
				System.out.println("Pal2PacE Path found ! " + found.get(0));
			} else {
				System.out.println("No Pal2PacE Path found ! ");
			}
		} else {
			System.out.println("Pal2PacE Path already set !");
		}
	}
	
	private static void findArma3ToolsFolder() {
		if (Config.getConfig().getString(Constants.CONFIG_arma3tools, null) == null || !PathFinder.validateArma3ToolsPath(Config.getConfig().getString(Constants.CONFIG_arma3tools, null))) {
			List<String> found = PathFinder.getPossibleArma3ToolsLocations();
			if (found.size() > 0) {
				Config.getConfig().setString(Constants.CONFIG_arma3tools, found.get(0).replace("\\", "/"));
				System.out.println("Arma 3 Tools Path found ! " + found.get(0));
			} else {
				System.out.println("No Arma 3 Tools Path found ! ");
			}
		} else {
			System.out.println("Arma 3 Tools Path already set !");
		}
	}
	private static void findDayZClientFolder() {
		if (Config.getConfig().getString(Constants.CONFIG_dayzclient, null) == null || !PathFinder.validateDayZClientPath(Config.getConfig().getString(Constants.CONFIG_dayzclient, null))) {
			List<String> found = PathFinder.getPossibleDayZClientLocations();
			if (found.size() > 0) {
				Config.getConfig().setString(Constants.CONFIG_dayzclient, found.get(0).replace("\\", "/"));
				System.out.println("DayZ Client Path found ! " + found.get(0));
			} else {
				System.out.println("No DayZ Client Path found ! ");
			}
		} else {
			System.out.println("DayZ Client Path already set !");
		}
	}

	private static void findPBOManagerFolder() {
		if (Config.getConfig().getString(Constants.CONFIG_pbomanager, null) == null || !PathFinder.validatePBOManagerPath(Config.getConfig().getString(Constants.CONFIG_pbomanager, null))) {
			List<String> found = PathFinder.getPossiblePBOManagerLocations();
			if (found.size() > 0) {
				Config.getConfig().setString(Constants.CONFIG_pbomanager, found.get(0).replace("\\", "/"));
				System.out.println("PBOManager Path found ! " + found.get(0));
			} else {
				System.out.println("No PBOManager Path found ! ");
			}
		} else {
			System.out.println("PBOManager Path already set !");
		}
	}

	private static void findDayZServerFolder() {
		if (Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder, null) == null || !PathFinder.validateDayZServerPath(Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder, null))) {
			List<String> found = PathFinder.getPossibleDayZServerLocations();
			if (found.size() > 0) {
				Config.getConfig().setString(Constants.CONFIG_lastDayZServerFolder, found.get(0).replace("\\", "/"));
				System.out.println("DayZ Server Path found ! " + found.get(0));
			} else {
				System.out.println("No DayZ Server Path found ! ");
			}
		} else {
			System.out.println("DayZ Server Path already set !");
		}
	}

	public static String getBuildString() {
		return mainVersion + "." + buildVersion + "." + buildId;
	}

	public static void setUIFont(FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource orig = (FontUIResource) value;
				Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}

	static {
		File mainClass = new File("src/de/lbmaster/dayztoolbox/MainClass.java");
		if (mainClass.exists() && mainClass.canRead() && mainClass.canWrite()) {
			try {
				String content = new String(Files.readAllBytes(mainClass.toPath()));
				String buildIdLine = null;
				for (String s : content.split("\n")) {
					if (s.contains("buildId")) {
						buildIdLine = s;
						break;
					}
				}
				int currentBuild = Integer.parseInt(buildIdLine.replaceAll("[^0-9]", ""));
				System.out.println(currentBuild);
				content = content.replace(buildIdLine, buildIdLine.replace(currentBuild + "", (currentBuild + 1) + ""));
				Files.write(mainClass.toPath(), content.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
