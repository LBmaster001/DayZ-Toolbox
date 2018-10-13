package de.lbmaster.dayztoolbox.guis.settingsgui;

import de.lbmaster.dayztoolbox.guis.CustomJButton;
import de.lbmaster.dayztoolbox.guis.maingui.MainGui;

public class CustomSettingsGuiButton extends CustomJButton {
	
	private static final long serialVersionUID = 1L;

	public CustomSettingsGuiButton(final MainGui gui) {
		super("Settings", SettingsGui.class);
	}
	
	public void setGui(MainGui gui) {
		((SettingsGui) dialog).setGui(gui);
	}
}
