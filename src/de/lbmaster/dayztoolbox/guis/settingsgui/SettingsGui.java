package de.lbmaster.dayztoolbox.guis.settingsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.maingui.MainGui;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;
import de.lbmaster.dayztoolbox.utils.UIDGenerator;

public class SettingsGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	protected JTextField textDayZServerFolder;
	protected JTextField textArma3ToolsFolder;
	protected JTextField textPBOManager;
	private JTextField textDayZClient;
	private JTextField textPal2PacE;
	
	private MainGui gui;

	public SettingsGui(String title) {
		super(title);
		setBounds(150, 130, 700, 345);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(100px;default)"),
				ColumnSpec.decode("max(150dlu;default):grow"),
				ColumnSpec.decode("max(70px;default)"),},
			new RowSpec[] {
				RowSpec.decode("50dlu"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),}));

		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSettings.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		contentPanel.add(lblSettings, "1, 1, 3, 1");

		JLabel lblDayzserverfolder = new JLabel("DayZServerFolder:");
		lblDayzserverfolder.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDayzserverfolder, "1, 2, right, default");

		textDayZServerFolder = new JTextField();
		contentPanel.add(textDayZServerFolder, "2, 2, fill, default");
		textDayZServerFolder.setColumns(10);

		JButton btnBrowseDayZ = new JButton("Browse...");
		contentPanel.add(btnBrowseDayZ, "3, 2");

		JLabel lblArmaTools = new JLabel("Arma3 Tools:");
		lblArmaTools.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblArmaTools, "1, 3, right, default");

		textArma3ToolsFolder = new JTextField();
		contentPanel.add(textArma3ToolsFolder, "2, 3, fill, default");
		textArma3ToolsFolder.setColumns(10);

		JButton btnBrowseArma = new JButton("Browse...");
		contentPanel.add(btnBrowseArma, "3, 3");

		JLabel lblPalpace = new JLabel("Pal2PacE:");
		contentPanel.add(lblPalpace, "1, 4, right, default");

		textPal2PacE = new JTextField();
		contentPanel.add(textPal2PacE, "2, 4, fill, default");
		textPal2PacE.setColumns(10);

		JButton btnBrowsePal2Pac = new JButton("Browse...");
		contentPanel.add(btnBrowsePal2Pac, "3, 4");

		JLabel lblPboManager = new JLabel("PBO Manager:");
		lblPboManager.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblPboManager, "1, 5, right, default");

		textPBOManager = new JTextField();
		contentPanel.add(textPBOManager, "2, 5, fill, default");
		textPBOManager.setColumns(10);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("Save");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
				if (gui != null)
				gui.updateButtons();
				close();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		close();

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(getDefaultCloseListener());
		buttonPane.add(cancelButton);
		close();

		JButton btnBrowsePBO = new JButton("Browse...");
		contentPanel.add(btnBrowsePBO, "3, 5");

		JLabel lblDayzClient = new JLabel("DayZ Client:");
		lblDayzClient.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDayzClient, "1, 6, right, default");

		textDayZClient = new JTextField();
		contentPanel.add(textDayZClient, "2, 6, fill, default");
		textDayZClient.setColumns(10);

		JButton btnBrowseClient = new JButton("Browse...");
		contentPanel.add(btnBrowseClient, "3, 6");

		if (Config.getConfig().getString(Constants.CONFIG_dayzclient, null) != null) {
			textDayZClient.setText(Config.getConfig().getString(Constants.CONFIG_dayzclient));
		}
		if (Config.getConfig().getString(Constants.CONFIG_pbomanager, null) != null) {
			textPBOManager.setText(Config.getConfig().getString(Constants.CONFIG_pbomanager));
		}
		if (Config.getConfig().getString(Constants.CONFIG_arma3tools, null) != null) {
			textArma3ToolsFolder.setText(Config.getConfig().getString(Constants.CONFIG_arma3tools));
			if (PathFinder.validateArma3ToolsPath(Config.getConfig().getString(Constants.CONFIG_arma3tools)))
				Config.getConfig().setString(Constants.CONFIG_pal2pace, Config.getConfig().getString(Constants.CONFIG_arma3tools) + "/TexView2");
		}
		if (Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder, null) != null) {
			textDayZServerFolder.setText(Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder));
		}
		if (Config.getConfig().getString(Constants.CONFIG_pal2pace, null) != null) {
			textPal2PacE.setText(Config.getConfig().getString(Constants.CONFIG_pal2pace));
		}

		btnBrowseClient.addActionListener(new CustomActionListener(textDayZClient, this));
		btnBrowseClient.setActionCommand("client");
		btnBrowsePBO.addActionListener(new CustomActionListener(textPBOManager, this));
		btnBrowsePBO.setActionCommand("pbo");
		btnBrowseDayZ.addActionListener(new CustomActionListener(textDayZServerFolder, this));
		btnBrowseDayZ.setActionCommand("dayz");
		btnBrowseArma.addActionListener(new CustomActionListener(textArma3ToolsFolder, this));
		btnBrowseArma.setActionCommand("arma3");
		btnBrowsePal2Pac.addActionListener(new CustomActionListener(textPal2PacE, this));
		btnBrowsePal2Pac.setActionCommand("pal2pac");
		
		JLabel lblUid = new JLabel("UID:");
		lblUid.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblUid, "1, 7");
		
		final JLabel uidlabel = new JLabel("");
		String uid = Config.getConfig().getString("uniqueID", null);
		if (uid == null || uid.length() != 64) {
			uid = UIDGenerator.generate64UID();
			Config.getConfig().setString("uniqueID", uid);
			Config.getConfig().write();
		}
		uidlabel.setText(uid);
		contentPanel.add(uidlabel, "2, 7");
		
		JButton btnCopy = new JButton("COPY");
		contentPanel.add(btnCopy, "3, 7");
		btnCopy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String uid = uidlabel.getText();
				if (uid == null || uid.length() != 64) {
					uid = UIDGenerator.generate64UID();
					Config.getConfig().setString("uniqueID", uid);
					Config.getConfig().write();
				}
				StringSelection selection = new StringSelection(uid);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});

		testIfValid();
	}
	
	public void setGui(MainGui gui) {
		this.gui = gui;
	}

	private void testIfValid() {
		String dayz = textDayZServerFolder.getText();
		String arma3 = textArma3ToolsFolder.getText();
		String pbo = textPBOManager.getText();
		String client = textDayZClient.getText();
		String pal2pac = textPal2PacE.getText();
		if (!PathFinder.validateDayZServerPath(dayz))
			textDayZServerFolder.setBackground(Color.RED);
		if (!PathFinder.validateArma3ToolsPath(arma3))
			textArma3ToolsFolder.setBackground(Color.RED);
		if (!PathFinder.validatePBOManagerPath(pbo))
			textPBOManager.setBackground(Color.RED);
		if (!PathFinder.validateDayZClientPath(client))
			textDayZClient.setBackground(Color.RED);
		if (!PathFinder.validatePal2PacEPath(pal2pac))
			textPal2PacE.setBackground(Color.RED);

	}

	private void save() {
		if (textDayZServerFolder.getText().length() > 1)
			Config.getConfig().setString(Constants.CONFIG_lastDayZServerFolder, textDayZServerFolder.getText());
		if (textArma3ToolsFolder.getText().length() > 1)
			Config.getConfig().setString(Constants.CONFIG_arma3tools, textArma3ToolsFolder.getText());
		if (textPBOManager.getText().length() > 1)
			Config.getConfig().setString(Constants.CONFIG_pbomanager, textPBOManager.getText());
		if (textDayZClient.getText().length() > 1)
			Config.getConfig().setString(Constants.CONFIG_dayzclient, textDayZClient.getText());
		if (textPal2PacE.getText().length() > 1)
			Config.getConfig().setString(Constants.CONFIG_pal2pace, textPal2PacE.getText());

	}
}

class CustomActionListener implements ActionListener {

	private JTextField input;
	private SettingsGui gui;

	public CustomActionListener(JTextField input, SettingsGui gui) {
		this.input = input;
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Browse...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setBounds(252, 41, 50, 19);
		String lastFile = input.getText();
		chooser.setCurrentDirectory(new File(lastFile));
		gui.add(chooser);

		int returnVal = chooser.showOpenDialog(gui);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			lastFile = folder.getAbsolutePath().replace("\\", "/");
			input.setText(lastFile);
			boolean valid = false;
			switch (e.getActionCommand()) {
			case "dayz":
				valid = PathFinder.validateDayZServerPath(folder.getAbsolutePath());
				break;
			case "arma3":
				valid = PathFinder.validateArma3ToolsPath(folder.getAbsolutePath());
				valid = PathFinder.validatePal2PacEPath(folder.getAbsolutePath() + "/TexView 2");
				break;
			case "pbo":
				valid = PathFinder.validatePBOManagerPath(folder.getAbsolutePath());
				break;
			case "client":
				valid = PathFinder.validateDayZClientPath(folder.getAbsolutePath());
				break;
			case "pal2pac":
				valid = PathFinder.validatePal2PacEPath(folder.getAbsolutePath());
				break;

			default:
				break;
			}
			System.out.println("Valid Path ? " + valid);
			if (!valid) {
				input.setBackground(Color.RED.brighter());
			} else {
				input.setBackground(Color.WHITE);
			}
		}
	}

}
