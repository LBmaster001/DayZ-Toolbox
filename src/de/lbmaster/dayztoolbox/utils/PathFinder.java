package de.lbmaster.dayztoolbox.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.lbmaster.dayz.configparser.BattleyeConfig;
import de.lbmaster.dayztoolbox.Constants;

public class PathFinder {

	private static List<String> getPossibleLocations(String[] allowed, boolean containsNoEquals, String endFile, boolean containsNoEqualsEndFile, boolean isFile, boolean caseSensitive, boolean returnParentFile) {
		File[] roots = File.listRoots();
		List<String> found = new ArrayList<String>();
		List<String> allowedArray = new ArrayList<String>();
		for (String all : allowed)
			allowedArray.add(all);

		for (File root : roots) {
			if (root.canRead()) {
				found.addAll(findFiles(root, allowedArray, containsNoEquals, caseSensitive));
			}
		}
		List<String> foundCopy = new ArrayList<String>();
		for (String s : found) {
			if (!caseSensitive) {
				s = s.toLowerCase();
				endFile = endFile.toLowerCase();
			}
			if (containsNoEqualsEndFile ? new File(s).getName().contains(endFile) : new File(s).getName().equals(endFile)) {
				if (isFile ? (new File(s).isFile() && returnParentFile ? validatePath(new File(s).getParent(), endFile) : true) : new File(s).isDirectory())
					foundCopy.add(returnParentFile ? new File(s).getParent() : s);
			}
		}
		for (String s : foundCopy) {
			System.out.println(s);
		}
		return foundCopy;
	}

	public static List<String> getPossibleDayZServerLocations() {
		return getPossibleLocations(new String[] { "steam", "program", "dayz", "game", "common", "app" }, true, "DayZServer", true, false, false, false);
	}

	public static List<String> getPossibleDayZClientLocations() {
		return getPossibleLocations(new String[] { "steam", "program", "dayz", "game", "common", "app" }, true, "DayZ", false, false, false, false);
	}

	public static List<String> getPossibleArma3ToolsLocations() {
		return getPossibleLocations(new String[] { "steam", "program", "arma", "game", "common", "app", "tools" }, true, "Arma3Tools.exe", true, true, false, true);
	}

	public static List<String> getPossiblePal2PacELocations() {
		return getPossibleLocations(new String[] { "steam", "program", "arma", "game", "common", "app", "tools", "bohemia", "texview", "pal2pac" }, true, "Pal2PacE.exe", true, true, false, true);
	}

	public static List<String> getPossiblePBOManagerLocations() {
		return getPossibleLocations(new String[] { "steam", "program", "pbo", "manager" }, true, "PBOManager.exe", true, true, false, true);
	}
	
	public static List<String> getPossibleWorkshopLocations() {
		return getPossibleLocations(new String[] { "steam", "program", "workshop", "game", "app", "content", "221100" }, true, "221100", false, false, false, false);
	}

	private static final List<String> findFiles(File folder, List<String> allowed, boolean containsNoEquals, boolean caseSensitive) {
		List<String> found = new ArrayList<String>();
		if (folder.listFiles() != null) {
			for (File entry : folder.listFiles()) {
				if (Files.isSymbolicLink(entry.toPath())) {
					continue;
				}
				if (allowed(entry.getName(), allowed, containsNoEquals, caseSensitive)) {
					found.add(entry.getAbsolutePath());
					if (entry.isDirectory()) {
						found.addAll(findFiles(entry, allowed, containsNoEquals, caseSensitive));
					}
				}
			}
		}
		return found;
	}

	private static boolean allowed(String name, List<String> allowed, boolean containsNoEquals, boolean caseSensitive) {
		for (String s : allowed) {
			if (!caseSensitive) {
				s = s.toLowerCase();
				name = name.toLowerCase();
			}
			if (containsNoEquals ? name.contains(s) : name.equals(s))
				return true;
		}
		return false;
	}

	public static boolean validatePal2PacEPath(String path) {
		return validatePath(path, "Pal2PacE.exe");
	}

	public static boolean validateDayZServerPath(String path) {
		return validatePath(path, "dayzserver_x64.exe");
	}

	public static boolean validateDayZClientPath(String path) {
		return validatePath(path, "dayz_x64.exe");
	}

	public static boolean validateArma3ToolsPath(String path) {
		return validatePath(path, "arma3tools.exe");
	}

	public static boolean validatePBOManagerPath(String path) {
		return validatePath(path, "pbomanager.exe");
	}

	public static boolean validatePath(String path, String contains) {
		if (path == null)
			return false;
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
			return false;
		for (File child : file.listFiles()) {
			if (child != null && child.getName().equalsIgnoreCase(contains))
				return true;
		}
		return false;
	}

	public static String findDayZToolBoxFolder() {
		return System.getProperty("user.home") + "/DayZTools";
	}
	
	public static String getNewestServerPath() {
		Map<String, String> servers = findAllServersWithVersion();
		int highest = 0;
		String best = "";
		for (Entry<String, String> entry : servers.entrySet()) {
			if (Integer.parseInt(entry.getValue().replaceAll("[^0-9]", "")) > highest) {
				best = entry.getKey();
			}
		}
		return best;
	}
	
	public static String getNewestClientPath() {
		Map<String, String> clients = findAllClientsWithVersion();
		int highest = 0;
		String best = "";
		for (Entry<String, String> entry : clients.entrySet()) {
			if (Integer.parseInt(entry.getValue().replaceAll("[^0-9]", "")) > highest) {
				best = entry.getKey();
			}
		}
		return best;
	}

	public static String getFittingClientPath(String version) {
		Map<String, String> clients = findAllClientsWithVersion();
		for (Entry<String, String> entry : clients.entrySet()) {
			if (entry.getValue().equals(version)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getFittingServerPath(String version) {
		Map<String, String> servers = findAllServersWithVersion();
		for (Entry<String, String> entry : servers.entrySet()) {
			if (entry.getValue().equals(version)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static Map<String, String> findAllServersWithVersion() {
		List<String> paths = getPossibleDayZServerLocations();
		Map<String, String> map = new HashMap<String, String>();
		for (String path : paths) {
			try {
				String version = findServer64BitVersion(path);
				String version2 = findServer32BitVersion(path);
				if (version != null && version.length() > 0) {
					map.put(toPathWithExe(path, "dayzserver_x64.exe"), version);
				}
				if (version2 != null && version2.length() > 0) {
					map.put(toPathWithExe(path, "dayzserver.exe"), version2);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static Map<String, String> findAllClientsWithVersion() {
		List<String> paths = getPossibleDayZClientLocations();
		Map<String, String> map = new HashMap<String, String>();
		for (String path : paths) {
			try {
				String version = findClient64BitVersion(path);
				String version2 = findClient32BitVersion(path);
				if (version != null && version.length() > 0) {
					map.put(toPathWithExe(path, "dayz_x64.exe"), version);
				}
				if (version2 != null && version2.length() > 0) {
					map.put(toPathWithExe(path, "dayz.exe"), version2);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public static String toPathWithExe(String folderPath, String exeName) {
		String path = folderPath.toLowerCase();
		if (!path.endsWith(exeName.toLowerCase())) {
			path += path.endsWith("/") ? exeName : "/" + exeName;
		}
		return path;
	}

	public static String findClient64BitVersion(String folderPath) throws IOException {
		File file = new File(toPathWithExe(folderPath, "dayz_x64.exe"));
		if (!file.exists()) {
			return "";
		}
		return findVersion(new String(Files.readAllBytes(file.toPath())));
	}

	public static String findClient32BitVersion(String folderPath) throws IOException {
		File file = new File(toPathWithExe(folderPath, "dayz.exe"));
		if (!file.exists()) {
			return "";
		}
		return findVersion(new String(Files.readAllBytes(file.toPath())));
	}

	public static String findServer64BitVersion(String serverPath) throws IOException {
		File file = new File(toPathWithExe(serverPath, "dayzserver_x64.exe"));
		if (!file.exists()) {
			return "";
		}
		return findVersion(new String(Files.readAllBytes(file.toPath())));
	}

	public static String findServer32BitVersion(String serverPath) throws IOException {
		File file = new File(toPathWithExe(serverPath, "dayzserver.exe"));
		if (!file.exists()) {
			return "";
		}
		return findVersion(new String(Files.readAllBytes(file.toPath())));
	}

	public static String findVersion(String content) {
		Matcher matcher = Pattern.compile("\\d{1}\\.\\d{2}\\.\\d{6}").matcher(content);
		Map<String, Integer> found = new HashMap<String, Integer>();
		while (matcher.find()) {
			String ver = matcher.group();
			int i = 0;
			if (found.containsKey(ver)) {
				i = found.get(ver);
				found.remove(ver);
			}
			i++;
			found.put(ver, i);
		}
		int max = 0;
		String version = "";
		for (Entry<String, Integer> entry : found.entrySet()) {
			if (entry.getValue() > max) {
				version = entry.getKey();
			}
		}
		return version;
	}
	
	public static String findBEConfigPath(Config serverCfg) {
		String name = new File(serverCfg.getFileLocation()).getName();
		name = name.substring(0, name.lastIndexOf("."));
		String path = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER) + "/profiles/BattlEye/" + name + "/BEServer_x64.cfg";
		return path;
	}
	
	public static BattleyeConfig findActiveBEConfig(Config serverCfg) throws IOException {
		File file = new File(findBEConfigPath(serverCfg));
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent != null && parent.isDirectory()) {
				String activeFile = null;
				for (File f : parent.listFiles()) {
					if (f.getName().toLowerCase().startsWith("beserver_x64_active")) {
						activeFile = f.getAbsolutePath();
						break;
					}
				}
				if (activeFile != null) {
					file = new File(activeFile);
				}
			}
		}
		if (file.exists()) {
			BattleyeConfig cfg = new BattleyeConfig(file);
			cfg.read();
			return cfg;
		}
		return null;
	}
	
	private static JsonObject modDependencies;
	
	public static JsonObject getModDependencies() {
		if (modDependencies == null)
			loadModDependencies();
		return modDependencies;
	}
	
	public static JsonObject loadModDependencies() {
		String userdir = System.getProperty("user.home");
		String steamjson = userdir + "/AppData/Local/DayZ Launcher/Steam.json";
		System.out.println("Launcher Path: " + steamjson);
		File jsonFile = new File(steamjson);
		if (jsonFile.exists()) {
			try {
				JsonObject obj = new JsonParser().parse(new String(Files.readAllBytes(jsonFile.toPath()))).getAsJsonObject();
				modDependencies = obj;
				return obj;
			} catch (JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
