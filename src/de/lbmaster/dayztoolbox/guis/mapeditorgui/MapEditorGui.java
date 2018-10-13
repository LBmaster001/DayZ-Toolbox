package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.mapcreatorgui.ErrorDialog;
import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapPositions;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class MapEditorGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private MapJPanel mapView;
	private MapPositionsTree rightPanel;
	private JButton btnAddPositionsFile;

	public MapEditorGui(String title) {
		super(title);
		setBounds(100, 100, 1000, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow"), ColumnSpec.decode("350px"), }, new RowSpec[] { RowSpec.decode("default:grow"), RowSpec.decode("23px"), }));

		btnAddPositionsFile = new JButton("Add Positions File");
		btnAddPositionsFile.setToolTipText("Currently Supported files: cfgplayerspawnpoints.xml and cfgeventspawns.xml");
		contentPanel.add(btnAddPositionsFile, "2, 2, right, default");

		btnAddPositionsFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Browse...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return ".xml";
					}

					@Override
					public boolean accept(File f) {
						return f != null && (f.getName().endsWith(".xml") || f.isDirectory());
					}
				});
				chooser.setBounds(252, 41, 50, 19);
				String lastFile = Config.getConfig().getString(Constants.CONFIG_lastDayZServerFolder);
				chooser.setCurrentDirectory(new File(lastFile));
				getContentPane().add(chooser);

				int returnVal = chooser.showOpenDialog(MapEditorGui.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File posFile = chooser.getSelectedFile();
					lastFile = posFile.getAbsolutePath().replace("\\", "/");
					if (posFile.isFile()) {
						if (mapView != null) {
							MapFile mapFile = mapView.getMapFile();
							if (posFile.getName().contains("event")) {
								try {
									for (MapPositions event : MapPositions.loadEventPositions(posFile)) {
										mapFile.addMapObject(event);
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} else if (posFile.getName().contains("player")) {
								MapPositions spawns = new MapPositions();
								try {
									spawns.loadFromPlayerPositionsFile(posFile);
									mapFile.addMapObject(spawns);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							if (rightPanel != null)
								contentPanel.remove(rightPanel);
							rightPanel = new MapPositionsTree(mapView.getMapFile());
							rightPanel.setMapRenderer(mapView);
							contentPanel.add(rightPanel, "2, 1, fill, fill");
							contentPanel.revalidate();
							revalidate();
						} else {
							displayError("No Map was loaded !");
						}
					}
				}
			}
		});

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow"), FormSpecs.DEFAULT_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.DEFAULT_COLSPEC, }, new RowSpec[] { FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("23px"), }));

		textField = new JTextField();
		buttonPane.add(textField, "1, 2, fill, center");
		textField.setText(Config.getConfig().getString(Constants.CONFIG_lastMapFile, PathFinder.findDayZToolBoxFolder() + "/mapfileexample.mff"));
		textField.setColumns(10);

		JButton btnBrowse = new JButton("Load...");
		buttonPane.add(btnBrowse, "2, 2, left, top");
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Browse...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return ".mff";
					}

					@Override
					public boolean accept(File f) {
						return f != null && (f.getName().endsWith(".mff") || f.isDirectory());
					}
				});
				chooser.setBounds(252, 41, 50, 19);
				String lastFile = textField.getText();
				chooser.setCurrentDirectory(new File(lastFile));
				getContentPane().add(chooser);

				int returnVal = chooser.showOpenDialog(MapEditorGui.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File folder = chooser.getSelectedFile();
					lastFile = folder.getAbsolutePath().replace("\\", "/");
					textField.setText(lastFile);
					load();
				}
			}
		});

		JButton btnSaveChanges = new JButton("Save changes");
		buttonPane.add(btnSaveChanges, "3, 2");
		btnSaveChanges.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (mapView != null) {
					if (mapView.getMapFile() != null) {
						try {
							mapView.getMapFile().save(new File(textField.getText()));
						} catch (IOException e) {
							e.printStackTrace();
						}
						displayInfo("File was saved successfuly to: " + mapView.getMapFile().getFile().getAbsolutePath());
						return;
					}
					displayError("No MapFile found !");
					return;
				}
				displayError("No MapFile found !");
				return;
			}
		});

		JButton cancelButton = new JButton("Close");
		buttonPane.add(cancelButton, "4, 2, left, top");
		getRootPane().setDefaultButton(cancelButton);
		cancelButton.addActionListener(getDefaultCloseListener());
	}

	public void loadMe() {
		System.out.println("loading");
		if (new File(textField.getText()).exists())
			load();
		else {
			displayError("Use the Map Creator to create a Map File or download my sample from: <a href=\"http://toast-teamspeak.de/dayztoolboxexamples/mapfileexample.mff\">mapfileexample.mff</a> and select it via the Browse... button.");
		}
	}

	private void displayError(String error) {
		new ErrorDialog(error, true).setVisible(true);
	}
	private void displayInfo(String info) {
		new ErrorDialog(info, false).setVisible(true);
	}

	private void load() {
		if (!new File(textField.getText()).exists())
			return;
		System.out.println("" + (mapView != null));
		if (mapView != null) {
			contentPanel.remove(mapView);
			removeComponentListener(mapView);
		}
		if (rightPanel != null)
			contentPanel.remove(rightPanel);
		Config.getConfig().setString(Constants.CONFIG_lastMapFile, textField.getText());
		contentPanel.revalidate();
		revalidate();
		mapView = new MapJPanel(textField.getText());
		contentPanel.add(mapView, "1, 1, 1, 2, fill, fill");
		addComponentListener(mapView);

		rightPanel = new MapPositionsTree(mapView.getMapFile());
		rightPanel.setMapRenderer(mapView);
		contentPanel.add(rightPanel, "2, 1, fill, fill");
		contentPanel.revalidate();
		revalidate();
	}

}
