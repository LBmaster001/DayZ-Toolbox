package de.lbmaster.dayz.configparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DayZModMetaConfig {

	private DayZConfig cfg;
	private String name;
	private String id;
	private boolean successfulyRead = false;
	
	public DayZModMetaConfig(String configLocation) {
		this.cfg = new DayZConfig(configLocation);
		init();
	}
	
	public DayZModMetaConfig(File file) {
		this.cfg = new DayZConfig(file);
		init();
	}
	
	private void init() {
		try {
			this.successfulyRead = this.cfg.read();
			this.name = cfg.getString("name", false);
			this.id = cfg.getFile().getParentFile().getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean wasLoadedSuccessfuly () {
		return successfulyRead;
	}
	
	public static List<DayZModMetaConfig> getAllModConfigs(String workshopDir) {
		String modsLocation = new File(workshopDir).getAbsolutePath();
		List<DayZModMetaConfig> mods = new ArrayList<DayZModMetaConfig>();
		if (modsLocation.endsWith("steamapps")) {
			modsLocation += "/workshop";
		}
		if (modsLocation.endsWith("workshop")) {
			modsLocation += "/content";
		}
		if (modsLocation.endsWith("content")) {
			modsLocation += "/221100";
		}
		if (modsLocation.endsWith("221100")) {
			File dayzworkshopFolder = new File(modsLocation);
			if (dayzworkshopFolder.exists() && dayzworkshopFolder.isDirectory()) {
				for (File moddirs : dayzworkshopFolder.listFiles()) {
					DayZModMetaConfig metaConfig = new DayZModMetaConfig(moddirs.getAbsolutePath() + "/meta.cpp");
					if (metaConfig.wasLoadedSuccessfuly()) {
						mods.add(metaConfig);
					}
				}
			}
		}
		return mods;
	}
	
}
