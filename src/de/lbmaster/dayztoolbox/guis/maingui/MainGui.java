package de.lbmaster.dayztoolbox.guis.maingui;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.MainClass;
import de.lbmaster.dayztoolbox.guis.economyconfiggui.CustomEconomyGuiButton;
import de.lbmaster.dayztoolbox.guis.mapcreatorgui.CustomMapGuiButton;
import de.lbmaster.dayztoolbox.guis.mapeditorgui.CustomMapEditorGuiButton;
import de.lbmaster.dayztoolbox.guis.playerdb.CustomPlayerDBGuiButton;
import de.lbmaster.dayztoolbox.guis.serverconfiggui.CustomServerGuiButton;
import de.lbmaster.dayztoolbox.guis.servermanager.CustomServerManagerGuiButton;
import de.lbmaster.dayztoolbox.guis.settingsgui.CustomSettingsGuiButton;
import de.lbmaster.dayztoolbox.guis.tips.CustomTipsGuiButton;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class MainGui extends JFrame {

	private static final long serialVersionUID = 1L;

	private static MainGui frame;

	private JPanel contentPane;
	private JButton btnServerConfigGui, btnEconomyConfigGui, btnSettingsConfigGui, btnTipsGui, btnMapCreatorGui,btnmapeditor, btnServerManagerGui;

	public static MainGui getFrame() {
		return frame;
	}

	public MainGui() {
		frame = this;
		setTitle("DayZ Modding Toolbox Version: " + MainClass.getBuildString());
		setLocation(120, 120);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		int paddingpx = 5;
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(168px;default)"),
				ColumnSpec.decode("max(168px;default)"),
				ColumnSpec.decode(paddingpx + "px"),
				ColumnSpec.decode("max(168px;default)"),
				ColumnSpec.decode("max(168px;default)"),},
			new RowSpec[] {
				RowSpec.decode("max(75px;min)"),
				RowSpec.decode("26px"),
				RowSpec.decode(paddingpx + "px"),
				RowSpec.decode("75px"),
				RowSpec.decode(paddingpx + "px"),
				RowSpec.decode("75px"),
				RowSpec.decode(paddingpx + "px"),
				RowSpec.decode("75px"),
				RowSpec.decode(paddingpx + "px"),
				RowSpec.decode("75px"),
				RowSpec.decode(paddingpx + "px"),
				RowSpec.decode("75px"),}));

		JLabel lblDayzModdingToolbox = new JLabel("DayZ Modding Toolbox");
		lblDayzModdingToolbox.setFont(new Font(Constants.FONT, Font.BOLD, 30));
		lblDayzModdingToolbox.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblDayzModdingToolbox, "1, 1, 4, 1");

		btnServerConfigGui = new CustomServerGuiButton();
		contentPane.add(btnServerConfigGui, "1, 4, 2, 1, fill, fill");

		btnSettingsConfigGui = new CustomSettingsGuiButton(this);
		contentPane.add(btnSettingsConfigGui, "4, 4, 2, 1, fill, fill");

		btnEconomyConfigGui = new CustomEconomyGuiButton();
		contentPane.add(btnEconomyConfigGui, "1, 6, 2, 1, fill, fill");
		
		btnMapCreatorGui = new CustomMapGuiButton();
		contentPane.add(btnMapCreatorGui, "1, 8, 2, 1, fill, fill");

		btnTipsGui = new CustomTipsGuiButton();
		contentPane.add(btnTipsGui, "4, 6, 2, 1, default, fill");

		btnServerManagerGui = new CustomServerManagerGuiButton();
		contentPane.add(btnServerManagerGui, "4, 8, 2, 1, default, fill");

		btnmapeditor = new CustomMapEditorGuiButton();
		contentPane.add(btnmapeditor, "1, 10, 2, 1, default, fill");
		
		btnmapeditor = new CustomPlayerDBGuiButton();
		contentPane.add(btnmapeditor, "1, 12, 2, 1, default, fill");

		updateButtons();
		pack();
		setResizable(false);

	}

	public void openUpdateDialog(boolean ignore) {
		if (!ignore) {
			long reminder = Long.parseLong(Config.getConfig().getString(Constants.CONFIG_REMINDER_AT, "-1"));
			if (System.currentTimeMillis() < reminder) {
				return;
			}
		}
		UpdaterGui updater = new UpdaterGui();
		updater.setVisible(true);
		updater.requestFocus();
	}

	public void updateButtons() {
		updateEconomyButton();
		updateServerSettingsButton();
		updateMapCreatorButton();
	}

	private void updateEconomyButton() {
		updateButton(btnEconomyConfigGui, Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER, "No DayZ Server Folder was found! set the Path in the Settings!");
	}

	private void updateServerSettingsButton() {
		updateButton(btnServerConfigGui, Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER, "No DayZ Server Folder was found! set the Path in the Settings!");
	}
	
	private void updateMapCreatorButton() {
		String pbomanager = Config.getConfig().getString(Constants.CONFIG_LOCATION_PBOMANAGER);
		String pal2pac = Config.getConfig().getString(Constants.CONFIG_LOCATION_PAL2PACE);
		if (pbomanager == null || !PathFinder.validatePBOManagerPath(pbomanager) || pal2pac == null || !PathFinder.validatePal2PacEPath(pal2pac)) {
			btnMapCreatorGui.setEnabled(false);
			String text = "";
			if (pbomanager == null || !PathFinder.validatePBOManagerPath(pbomanager))
				text += "PBOManager not found ! Go to the Tips to see where to download the PBOManager. ";
			if (pal2pac == null || !PathFinder.validatePal2PacEPath(pal2pac))
				text += "Pal2PacE.exe not found ! Go to the Tips to see where to download the Pal2Pac";
			btnMapCreatorGui.setToolTipText(text);
		} else {
			btnMapCreatorGui.setEnabled(true);
			btnMapCreatorGui.setToolTipText("");
		}
	}

	private void updateButton(JButton button, String configPath, String tooltip) {
		String serverFolder = Config.getConfig().getString(configPath);
		if (serverFolder == null || !PathFinder.validateDayZServerPath(serverFolder)) {
			button.setEnabled(false);
			button.setToolTipText(tooltip);
		} else {
			button.setEnabled(true);
			button.setToolTipText("");
		}
	}
}
