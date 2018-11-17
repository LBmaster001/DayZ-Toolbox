package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.mapcreatorgui.ErrorDialog;
import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;
import de.lbmaster.dayztoolbox.utils.Config;

import java.awt.Choice;
import javax.swing.JTextField;

public class MapExportDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	public MapExportDialog(final MapEditorGui parent) {
		super("Export to xml");
		setBounds(100, 100, 450, 184);
		if (parent != null)
			setLocationRelativeTo(parent);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		final Choice choice = new Choice();
		choice.add("Player Spawnpoints");
		choice.add("Event Spawnpoints");
		choice.setBounds(10, 36, 414, 20);
		contentPanel.add(choice);
		
		textField = new JTextField();
		textField.setBounds(10, 62, 315, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.setBounds(335, 62, 89, 23);
		contentPanel.add(btnBrowse);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String location = textField.getText();
				if (location != null && location.length() > 1) {
					if (choice.getSelectedItem().equals("Player Spawnpoints")) {
						exportPlayerSpawns(new File(location), parent);
					} else if (choice.getSelectedItem().equals("Event Spawnpoints")) {
						exportEventSpawns(new File(location), parent);
					}
				}
			}
		});
		
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Browse...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				String lastFile = Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder);
				chooser.setCurrentDirectory(new File(lastFile));
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return ".xml";
					}

					@Override
					public boolean accept(File f) {
						return f != null && ((f.getName().endsWith(".xml") && (f.getName().toLowerCase().contains("cfgeventspawns") || f.getName().toLowerCase().contains("cfgplayerspawnpoints"))) || f.isDirectory());
					}
				});
				chooser.setBounds(252, 41, 50, 19);
				getContentPane().add(chooser);

				int returnVal = chooser.showOpenDialog(MapExportDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File folder = chooser.getSelectedFile();
					lastFile = folder.getAbsolutePath().replace("\\", "/");
					textField.setText(lastFile);
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(getDefaultCloseListener());
		buttonPane.add(cancelButton);

	}
	
	private void exportPlayerSpawns(File to, MapEditorGui parent) {
		if (!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		MapPositions spawns = parent.getMapPanel().getMapFile().getPositionsByName(MapPositions.MAP_POSITIONS_NAME_PLAYER_SPAWNPOINTS);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<playerspawnpoints>\r\n" + 
				"    <spawn_params>\r\n" + 
				"        <min_dist_zombie>30.0</min_dist_zombie>\r\n" + 
				"        <max_dist_zombie>70.0</max_dist_zombie>\r\n" + 
				"        <min_dist_player>25.0</min_dist_player>\r\n" + 
				"        <max_dist_player>70.0</max_dist_player>\r\n" + 
				"        <min_dist_static>0.5</min_dist_static>\r\n" + 
				"        <max_dist_static>2.0</max_dist_static>\r\n" + 
				"    </spawn_params>\r\n" + 
				"\r\n" + 
				"    <!-- generate params -->\r\n" + 
				"    <!-- used when no file with generated points is found -->\r\n" + 
				"    <generator_params>\r\n" + 
				"        <grid_density>0.125</grid_density>\r\n" + 
				"        <grid_width>40.0</grid_width>\r\n" + 
				"        <grid_height>40.0</grid_height>\r\n" + 
				"        <min_dist_water>4.0</min_dist_water>\r\n" + 
				"        <max_dist_water>20.0</max_dist_water>\r\n" + 
				"        <min_dist_static>0.5</min_dist_static>\r\n" + 
				"        <max_dist_static>2.0</max_dist_static>\r\n" + 
				"        <min_steepness>-0.785398163</min_steepness>\r\n" + 
				"        <max_steepness>0.785398163</max_steepness>\r\n" + 
				"    </generator_params>\r\n" + 
				"\r\n" + 
				"    <generator_posbubbles>\r\n");
		if (spawns != null) {
			for (MapPosition pos : spawns.getPositions()) {
				sb.append("\t\t" + pos.toDayZReadableFormatPlayerSpawn() + "\r\n");
			}
		}
		sb.append("    </generator_posbubbles>\r\n" + 
				"</playerspawnpoints>");
		try {
			Files.write(to.toPath(), sb.toString().getBytes());
			setVisible(false);
			new ErrorDialog("Player Spawns successfully written to: " + to.getAbsolutePath(), false).setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void exportEventSpawns(File to, MapEditorGui parent) {
		if (!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<MapPositions> spawns = parent.getMapPanel().getMapFile().getAllEventSpawns();
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\r\n" + 
				"<eventposdef>\r\n");
		for (MapPositions positions : spawns) {
			List<MapPosition> poses = positions.getPositions();
			sb.append("\t<event name=\"" + positions.getName() + "\"" + (poses.size() <= 0 ? " /" : "") + ">\r\n");
			if (poses.size() <= 0)
				continue;
			for (MapPosition pos : poses) {
				sb.append("\t\t" + pos.toDayZReadableFormatEventSpawn() + "\r\n");
			}
			sb.append("\t</event>\r\n");
		}
		sb.append("</eventposdef>");
		try {
			Files.write(to.toPath(), sb.toString().getBytes());
			setVisible(false);
			new ErrorDialog("Event Spawns successfully written to: " + to.getAbsolutePath(), false).setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
