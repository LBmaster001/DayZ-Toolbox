package de.lbmaster.dayztoolbox.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
			System.out.println(s);
			if (containsNoEqualsEndFile ? new File(s).getName().contains(endFile) : new File(s).getName().equals(endFile)) {
				if (isFile ? (new File(s).isFile() && returnParentFile ? validatePath(new File(s).getParent(), endFile) : true) : new File(s).isDirectory())
					foundCopy.add(returnParentFile ? new File(s).getParent() : s);
			}
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

	private static final List<String> findFiles(File folder, List<String> allowed, boolean containsNoEquals, boolean caseSensitive) {
		List<String> found = new ArrayList<String>();
		if (folder.listFiles() != null) {
			for (File entry : folder.listFiles()) {
				if (allowed(entry.getName(), allowed, containsNoEquals, caseSensitive)) {
					if (entry.isDirectory()) {
						found.add(entry.getAbsolutePath());
						found.addAll(findFiles(entry, allowed, containsNoEquals, caseSensitive));
					} else {
						found.add(entry.getAbsolutePath());
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
}
