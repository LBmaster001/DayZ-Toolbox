package de.lbmaster.dayz.configparser;

import java.io.File;

import de.lbmaster.dayztoolbox.utils.UIDGenerator;

public class DefaultBattleyeConfig extends BattleyeConfig {

	public DefaultBattleyeConfig(File file) {
		super(file);
		setDefaultValues();
	}
	
	private void setDefaultValues() {
		setString("RConPassword", UIDGenerator.generateUID(16));
		setInt("RestrictRCon", 0);
	}

}
