package de.lbmaster.dayztoolbox.mods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.lbmaster.dayz.configparser.DayZConfig;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class DayZMod {

	private File modRootFolder;
	private String modName;
	private boolean clientMod, serverMod, isMod = false, isWS = false;
	private List<String> dependencies = new ArrayList<String>();

	private static List<DayZMod> allMods = new ArrayList<DayZMod>();

	public static List<DayZMod> findAllWorkshopMods() {
		allMods.clear();
		List<DayZMod> mods = new ArrayList<DayZMod>();
		for (String workshopLoc : PathFinder.getPossibleWorkshopLocations()) {
			File workshop = new File(workshopLoc);
			if (!workshop.isDirectory())
				continue;
			for (File modLoc : workshop.listFiles()) {
				try {
					DayZMod mod = new DayZMod(modLoc.getAbsolutePath());
					mod.isWS = true;
					mods.add(mod);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return mods;
	}
	
	public static DayZMod getModByFolderName(String name) {
		for (DayZMod mod : allMods) {
			if (mod != null && mod.getModRootFolder() != null && mod.getModRootFolder().getName().equalsIgnoreCase(name)) {
				return mod;
			}
		}
		return null;
	}

	public DayZMod(String modlocation) throws IOException {
		if (findRootModFolder(modlocation)) {
			isMod = true;
			determineClientAndOrServerMod();
			readMetaCpp();
			readModCpp();
			resolveDependencies();
			allMods.add(this);
		}
	}
	
	public boolean isWorkshopMod() {
		return isWS;
	}

	public boolean isMod() {
		return isMod;
	}
	
	private void resolveDependencies() {
		JsonObject obj = PathFinder.getModDependencies();
		if (obj == null)
			return;
		JsonArray arr = obj.get("Extensions").getAsJsonArray();
		this.dependencies.clear();
		if (arr != null && arr.size() > 0) {
			String steamid = modRootFolder.getName();
			for (int i = 0; i < arr.size(); i++) {
				JsonElement element = arr.get(i);
				if (element == null)
					continue;
				if (element.getAsJsonObject().get("Id").getAsString().equals("steam:" + steamid)) {
					JsonArray dependencies = element.getAsJsonObject().get("SteamDependencies").getAsJsonArray();
					if (dependencies == null)
						continue;
					for (int a = 0; a < dependencies.size(); a++) {
						this.dependencies.add(dependencies.get(a).getAsString());
					}
					break;
				}
			}
		}
	}

	private void determineClientAndOrServerMod() {
		if (this.modRootFolder == null)
			return;
		File keys = new File(modRootFolder.getAbsolutePath() + "/keys");
		this.serverMod = true;
		this.clientMod = keys.exists() && keys.isDirectory();
	}

	private boolean readModCpp() throws IOException {
		if (this.modRootFolder == null)
			return false;
		File moddcpp = new File(modRootFolder.getAbsolutePath() + "/mod.cpp");
		if (!moddcpp.exists()) {
			return false;
		}
		DayZConfig mod = new DayZConfig(moddcpp);
		boolean ok = mod.read();
		if (!ok)
			return false;
		if (this.modName == null)
			this.modName = mod.getString("CfgMods..name", false, null);
		return true;
	}

	private boolean readMetaCpp() throws IOException {
		if (this.modRootFolder == null)
			return false;
		File metacpp = new File(modRootFolder.getAbsolutePath() + "/meta.cpp");
		if (!metacpp.exists()) {
			return false;
		}
		DayZConfig meta = new DayZConfig(metacpp);
		boolean ok = meta.read();
		if (!ok)
			return false;
		if (this.modName == null)
			this.modName = meta.getString("name", false, null);
		return true;
	}

	private boolean findRootModFolder(String modlocation) {
		File root = new File(modlocation);
		if (root.isFile()) {
			root = root.getParentFile();
		}
		if (root == null)
			return false;
		boolean isRoot = false;
		do {
			if (root == null || !root.isDirectory())
				break;
			for (File f : root.listFiles()) {
				if (f.getName().toLowerCase().contains("addons")) {
					isRoot = true;
				}
			}
		} while (!isRoot);
		this.modRootFolder = root;
		return root != null;
	}

	public File getModRootFolder() {
		return modRootFolder;
	}

	public String getFullModname() {
		StringBuilder sb = new StringBuilder();
		for (String dependency : dependencies) {
			DayZMod mod = findDayZModByName(dependency);
			if (mod != null) {
				sb.append(mod.getModName() + ", ");
			}
		}
		String dependencies = sb.toString();
		if (dependencies.length() > 0) {
			dependencies = " (" + dependencies.substring(0, dependencies.length() - 2) + ")";
		}
		if (this.modName == null)
			return this.modRootFolder.getName() + dependencies;

		return modName + dependencies;
	}

	public String getModName() {
		if (this.modName == null)
			return this.modRootFolder.getName();
		return modName;
	}

	public boolean isClientMod() {
		return clientMod;
	}

	public boolean isServerMod() {
		return serverMod;
	}

	private static DayZMod findDayZModByName(String folderName) {
		for (DayZMod loadedMod : allMods) {
			if (loadedMod.modRootFolder.getName().equalsIgnoreCase(folderName) || loadedMod.getModName().equalsIgnoreCase(folderName))
				return loadedMod;
		}
		return null;
	}
	
	public List<String> getDependencies() {
		return dependencies;
	}
	
	public boolean hasAllDepencencies(List<String> loadedMods) { // Not working Properly ! Compares Path with Folder Name
		if (loadedMods == null || this.dependencies == null)
			return false;
		for (String dependency : this.dependencies) {
			if (dependency != null) {
				boolean contains = false;
				for (String loaded : loadedMods) {
					if (loaded.endsWith(dependency))
						contains = true;
				}
				if (!contains)
					return false;
			}
		}
		return true;
	}
}
