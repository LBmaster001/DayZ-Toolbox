package de.lbmaster.dayztoolbox.guis.economyconfiggui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.utils.Config;

import java.awt.Choice;

public class EconomyConfigGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private JCheckBox[][] checkBoxes;
	private Choice choice;
	private CheckBoxGrid grid = null;

	public EconomyConfigGui(String title) {
		super(title);
		setSize(550, 405);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("max(130px;default)"), ColumnSpec.decode("120px"), ColumnSpec.decode("90px"), ColumnSpec.decode("90px"), ColumnSpec.decode("90px"), }, new RowSpec[] { RowSpec.decode("50dlu"), RowSpec.decode("30px"),
				RowSpec.decode("30px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"), }));

		JLabel lblEconomyxmlGui = new JLabel("Economy.xml Gui");
		lblEconomyxmlGui.setHorizontalAlignment(SwingConstants.CENTER);
		lblEconomyxmlGui.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		contentPanel.add(lblEconomyxmlGui, "1, 1, 5, 1");

		JLabel lblMission = new JLabel("Mission:");
		lblMission.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMission, "1, 2");

		choice = new Choice();
		contentPanel.add(choice, "2, 2, 2, 1");
		refreshChoice();
		int index = getItemIndex(Config.getConfig().getString(Constants.CONFIG_LAST_MISSION_SELECTED));
		System.out.println("Choice index: " + index + " " + Config.getConfig().getString(Constants.CONFIG_LAST_MISSION_SELECTED));
		if (index >= 0) {
			choice.select(index);
		}
		choice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				loadEconomyFile();
				Config.getConfig().setString(Constants.CONFIG_LAST_MISSION_SELECTED, choice.getSelectedItem());
			}
		});

		loadEconomyFile();

		JLabel lblInitstartup = new JLabel("Init (Startup)");
		lblInitstartup.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblInitstartup, "2, 3");

		JLabel lblLoadFromDb = new JLabel("Load from DB");
		lblLoadFromDb.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblLoadFromDb, "3, 3");

		JLabel lblRespawn = new JLabel("Respawn");
		lblRespawn.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblRespawn, "4, 3");

		JLabel lblSaveInDb = new JLabel("Save in DB");
		lblSaveInDb.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblSaveInDb, "5, 3");

		JLabel lblDynamicloot = new JLabel("Dynamic (Loot)");
		lblDynamicloot.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblDynamicloot, "1, 4");

		JLabel lblAnimals = new JLabel("Animals");
		lblAnimals.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblAnimals, "1, 5");

		JLabel lblZombies = new JLabel("Zombies");
		lblZombies.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblZombies, "1, 6");

		JLabel lblVehicles = new JLabel("Vehicles");
		lblVehicles.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblVehicles, "1, 7");

		JLabel lblRandomcrashsites = new JLabel("Random (Crashsites)");
		lblRandomcrashsites.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblRandomcrashsites, "1, 8");

		JLabel lblCustom = new JLabel("Custom");
		lblCustom.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblCustom, "1, 9");

		JLabel lblBuilding = new JLabel("Building");
		lblBuilding.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblBuilding, "1, 10");

		JLabel lblPlayers = new JLabel("Players");
		lblPlayers.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblPlayers, "1, 11");

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("Save");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (grid != null) {
					try {
						File economyXmlFile = getXMLFilePath();
						if (economyXmlFile != null)
							Files.write(economyXmlFile.toPath(), grid.toXml().getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					close();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
	}

	private int getItemIndex(String item) {
		for (int i = 0; i < choice.getItemCount(); i++) {
			if (choice.getItem(i).equals(item))
				return i;
		}
		return -1;
	}

	private void refreshChoice() {
		String dayzFolder = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
		if (dayzFolder == null)
			return;
		choice.removeAll();
		File mpmissions = new File(dayzFolder + "/mpmissions");
		if (mpmissions.exists() && mpmissions.isDirectory()) {
			for (File f : mpmissions.listFiles()) {
				if (f.isDirectory())
					choice.add(f.getName());
			}
		} else {
			System.out.println("MPMissions Folder not found or is a file !");
		}
	}

	private File getXMLFilePath() {
		String dayzFolder = Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER);
		if (dayzFolder == null)
			return null;
		String folder = dayzFolder + "/mpmissions/" + choice.getSelectedItem();
		File economyXmlFile = new File(folder + "/db/economy.xml");
		return economyXmlFile;
	}

	private void loadEconomyFile() {
		File economyXmlFile = getXMLFilePath();
		if (economyXmlFile != null && economyXmlFile.exists()) {
			try {
				String xml = new String(Files.readAllBytes(economyXmlFile.toPath()));
				if (grid == null)
					grid = new CheckBoxGrid(xml);
				else
					grid.loadXML(xml);
				grid.addToPanel(contentPanel, 2, 4);
				System.out.println(grid.toXml());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Economy File in " + choice.getSelectedItem() + " not found !");
		}
	}

	public JCheckBox getCheckBox(int col, int row) {
		return checkBoxes[col][row];
	}

}
