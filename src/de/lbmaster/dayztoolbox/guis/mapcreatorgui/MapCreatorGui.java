package de.lbmaster.dayztoolbox.guis.mapcreatorgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;
import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapImage;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PBOManager;
import de.lbmaster.dayztoolbox.utils.Pal2PacE;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class MapCreatorGui extends CustomDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Thread runningThread = null;
	private boolean stop = false;
	private JButton okButton;
	private JCheckBox chckbxDefaultMap;

	private JProgressBar progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBar;

	public MapCreatorGui(String title) {
		super(title);
		setSize(650, 345);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("120px"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("100px"), },
				new RowSpec[] { RowSpec.decode("50dlu"), RowSpec.decode("30px"), RowSpec.decode("30px"), RowSpec.decode("30px"), RowSpec.decode("30px"), RowSpec.decode("30px"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblMapCreatorGui = new JLabel("Map Creator GUI");
		lblMapCreatorGui.setHorizontalAlignment(SwingConstants.CENTER);
		lblMapCreatorGui.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		contentPanel.add(lblMapCreatorGui, "1, 1, 2, 1");

		JLabel lblAddonsFound = new JLabel("Addons found ");
		lblAddonsFound.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAddonsFound, "1, 2");

		progressBar = new JProgressBar();
		contentPanel.add(progressBar, "2, 2, 2, 1");

		JLabel lblPboUnpacked = new JLabel("PBO Unpacked ");
		lblPboUnpacked.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblPboUnpacked, "1, 3");

		progressBar_1 = new JProgressBar();
		progressBar_1.setMaximum(1000);
		contentPanel.add(progressBar_1, "2, 3, 2, 1");

		JLabel lblImagesConverted = new JLabel("Images Converted ");
		lblImagesConverted.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblImagesConverted, "1, 4");

		progressBar_2 = new JProgressBar();
		progressBar_2.setMaximum(1000);
		contentPanel.add(progressBar_2, "2, 4, 2, 1");

		JLabel lblImagesMerged = new JLabel("Images Merged ");
		lblImagesMerged.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblImagesMerged, "1, 5");

		progressBar_3 = new JProgressBar();
		progressBar_3.setMaximum(1000);
		contentPanel.add(progressBar_3, "2, 5, 2, 1");

		JLabel lblImageSaved = new JLabel("Image Saved ");
		lblImageSaved.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblImageSaved, "1, 6");

		progressBar_4 = new JProgressBar();
		contentPanel.add(progressBar_4, "2, 6, 2, 1");

		chckbxDefaultMap = new JCheckBox("Default Map");
		chckbxDefaultMap.setSelected(Config.getConfig().getBoolean(Constants.CONFIG_DEFAULT_MAP_FOLDER, true));
		contentPanel.add(chckbxDefaultMap, "1, 8");

		final JButton btnBrowse = new JButton("Browse...");
		contentPanel.add(btnBrowse, "3, 8");

		textField = new JTextField(Config.getConfig().getString(Constants.CONFIG_LOCATION_DAYZCLIENT) + "/Addons" + "/worlds_chernarusplus_data.pbo");
		if (Config.getConfig().getString(Constants.CONFIG_LAST_MAP_FOLDER) != null && !chckbxDefaultMap.isSelected()) {
			textField.setText(Config.getConfig().getString(Constants.CONFIG_LAST_MAP_FOLDER));
			textField.setEditable(true);
			chckbxDefaultMap.setSelected(false);
			btnBrowse.setEnabled(true);
		} else {
			textField.setEditable(false);
			btnBrowse.setEnabled(false);
			chckbxDefaultMap.setSelected(true);
		}
		contentPanel.add(textField, "2, 8, fill, default");
		textField.setColumns(10);

		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Browse...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return ".pbo";
					}

					@Override
					public boolean accept(File f) {
						return f != null && (f.getName().endsWith(".pbo") || f.isDirectory());
					}
				});
				chooser.setBounds(252, 41, 50, 19);
				String lastFile = textField.getText();
				chooser.setCurrentDirectory(new File(lastFile));
				getContentPane().add(chooser);

				int returnVal = chooser.showOpenDialog(MapCreatorGui.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File folder = chooser.getSelectedFile();
					lastFile = folder.getAbsolutePath().replace("\\", "/");
					textField.setText(lastFile);
					Config.getConfig().setString(Constants.CONFIG_LAST_MAP_FOLDER, lastFile);
				}
			}
		});

		chckbxDefaultMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Config.getConfig().setBoolean(Constants.CONFIG_DEFAULT_MAP_FOLDER, chckbxDefaultMap.isSelected());
				if (!chckbxDefaultMap.isSelected()) {
					textField.setEditable(true);
					btnBrowse.setEnabled(true);
					textField.setText(Config.getConfig().getString(Constants.CONFIG_LAST_MAP_FOLDER, ""));
				} else {
					textField.setEditable(false);
					btnBrowse.setEnabled(false);
					textField.setText(Config.getConfig().getString(Constants.CONFIG_LOCATION_DAYZCLIENT) + "/Addons" + "/worlds_chernarusplus_data.pbo");
				}
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("Start");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);

		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (runningThread != null && runningThread.isAlive()) {
					stop = true;
					runningThread.interrupt();
				}
				System.out.println("RunningThread interupted ? " + (runningThread != null ? runningThread.isInterrupted() + "" : "null !"));
				close();
			}
		});
		buttonPane.add(cancelButton);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		asyncProcessing();
	}

	private long estimateSize(String folder) {
		File f = new File(folder);
		long size = 0;
		for (File f2 : f.listFiles()) {
			size += f2.length();
		}
		return (long) (size * 14.7);
	}

	private boolean displayPBOProcess = false;
	private JTextField textField;

	private void asyncProcessing() {
		runningThread = new Thread(new Runnable() {

			@Override
			public void run() {
				resetBars();
				okButton.setEnabled(false);
				String clientFolder = Config.getConfig().getString(Constants.CONFIG_LOCATION_DAYZCLIENT);
				if (!chckbxDefaultMap.isSelected()) {
					Config.getConfig().setString(Constants.CONFIG_LAST_MAP_FOLDER, textField.getText());
					clientFolder = Config.getConfig().getString(Constants.CONFIG_LAST_MAP_FOLDER);
				}
				if (clientFolder == null) {
					progressBar.setValue(0);
					ErrorDialog.displayError("DayZ Client Folder not set! Go to Settings and set the DayZ Client Folder value");
					return;
				}
				if (clientFolder.endsWith(".pbo")) {
					clientFolder = new File(clientFolder).getParent();
				}
				File addons = new File(clientFolder + (clientFolder.toLowerCase().endsWith("addons") ? "" : "/Addons"));
				if (!addons.exists()) {
					progressBar.setValue(50);
					ErrorDialog.displayError("Addons Folder not found !");
					return;
				}
				progressBar.setValue(100);
				progressBar_1.setValue(0);
				if (stop)
					return;

				String pboManagerPath = Config.getConfig().getString(Constants.CONFIG_LOCATION_PBOMANAGER);
				if (pboManagerPath == null) {
					progressBar_1.setValue(0);
					ErrorDialog.displayError("PBOManager Folder not set! Go to Settings and set the PBOManager Folder value");
					return;
				}
				if (!PathFinder.validatePBOManagerPath(pboManagerPath)) {
					progressBar_1.setValue(50);
					ErrorDialog.displayError("PBOManager not found! Go to Settings and set the PBOManager Folder value");
					return;
				}
				File mapAddon = new File(addons.getAbsolutePath() + "/worlds_chernarusplus_data.pbo");
				if (!chckbxDefaultMap.isSelected()) {
					String filename = null;
					for (File f : addons.listFiles()) {
						if (f.getName().endsWith(".pbo")) {
							try {
								if (hasLayers(f)) {
									filename = f.getName();
									break;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					if (filename == null) {
						ErrorDialog.displayError("Map File no found !");
						return;
					}
					mapAddon = new File(addons.getAbsolutePath() + "/" + filename);
				}
				if (!mapAddon.exists()) {
					progressBar_1.setValue(60);
					ErrorDialog.displayError("Map File not found ! Please check your addons folder for \"" + mapAddon.getName() + "\"");
					return;
				}
				if (stop)
					return;
				String folderFile = mapAddon.getAbsolutePath();
				File mapFolder = new File(folderFile.substring(0, folderFile.length() - 4));
				System.out.println("MapFolder: " + mapFolder.getAbsolutePath());
				if (!mapFolder.exists()) {
					PBOManager pbomgr = new PBOManager(pboManagerPath);
					displayPBOProcess = true;
					pbomgr.exctract(mapFolder.getAbsolutePath());
					File layers = findLayersFolder(mapFolder);
					if (layers == null) {
						ErrorDialog.displayError("Error while processing Images. PBO not unpacked ?");
						System.err.println("Map Folder: " + mapFolder);
						return;
					}
					displayPBOProcess(layers.getAbsolutePath());
					displayPBOProcess = false;
				}
				if (stop)
					return;
				if (!mapFolder.exists()) {
					progressBar_1.setValue(75);
					ErrorDialog.displayError("Failed to extract the PBO File !");
					return;
				}
				progressBar_1.setValue(progressBar_1.getMaximum());

				Pal2PacE pal2pace = Pal2PacE.findPal2PacE();
				if (pal2pace == null) {
					progressBar_2.setValue(0);
					ErrorDialog.displayError("No Pal2PacE.exe found! Check your Arma 3 Tools Folder! Cannot convert PAA Files to PNG !");
					return;
				} else {
					System.out.println("Pal2PacE.exe found ! " + pal2pace.getPal2PacELocation());
				}
				if (stop)
					return;

				File data_dir = new File(findLayersFolder(mapFolder).getAbsolutePath());
				if (!data_dir.exists()) {
					progressBar_2.setValue(0);
					ErrorDialog.displayError("Malformed Word Data Folder ! No Layers Folder found !");
					return;
				}
				int xMax = 0;
				int yMax = 0;
				File pngOutput = new File(data_dir.getAbsolutePath() + "/tempPNGs");
				if (!pngOutput.exists()) {
					if (!pngOutput.mkdirs() || !pngOutput.isDirectory()) {
						System.out.println("Temp Directory creation Error !");
						return;
					} else {
						System.out.println("Temp Directory successfuly created");
					}
				}
				int conversions = getPendingConversionsCount(data_dir);
				int index = 0;
				for (File f2 : data_dir.listFiles()) {
					if (f2.getName().toLowerCase().startsWith("s_")) {
						String[] name = f2.getName().split("_");
						int x = Integer.parseInt(name[1]);
						int y = Integer.parseInt(name[2]);
						if (x > xMax)
							xMax = x;
						if (y > yMax)
							yMax = y;
						progressBar_2.setValue(progressBar_2.getMaximum() * index++ / conversions);
						String outputFile = data_dir.getAbsolutePath() + "/tempPNGs/" + f2.getName().substring(0, f2.getName().lastIndexOf(".")) + ".png";
						if (new File(outputFile).exists())
							continue;
						System.out.println(outputFile);
						pal2pace.paaToPng(f2.getAbsolutePath(), outputFile);
						if (stop)
							return;
					}
				}
				progressBar_2.setValue(progressBar_2.getMaximum());
				BufferedImage firstImg = null;
				try {
					if (pngOutput.listFiles().length <= 0) {
						ErrorDialog.displayError("Something went wrong while converting the images. Please report this error.");
						return;
					}
					firstImg = ImageIO.read(pngOutput.listFiles()[0]);
					System.out.println("FirstFile: " + pngOutput.listFiles()[0].getAbsolutePath());
					int correctionPixelsOff = 16;
					int imageWidth = firstImg.getWidth() - correctionPixelsOff * 2;
					int imageHeight = firstImg.getHeight() - correctionPixelsOff * 2;
					BufferedImage img = new BufferedImage(imageWidth * (xMax + 1), imageHeight * (yMax + 1), BufferedImage.TYPE_INT_RGB);
					Graphics g = img.createGraphics();
					int index2 = 0;
					for (File image : pngOutput.listFiles()) {
						System.out.println("Processing " + image.getName());
						String[] name = image.getName().split("_");
						int x = Integer.parseInt(name[1]);
						int y = Integer.parseInt(name[2]);
						BufferedImage imgAdd = ImageIO.read(image);
						g.drawImage(imgAdd, x * imageWidth - correctionPixelsOff, y * imageHeight - correctionPixelsOff, null);
						progressBar_3.setValue(progressBar_3.getMaximum() * index2++ / index);

						if (stop)
							return;
					}
					int xEnd = 0;
					int lastColor = 0;
					int same = 0;
					for (int i = 0; i < Math.min(img.getWidth(), img.getHeight()); i++) {
						int color = img.getRGB(i, i);
						if (lastColor == color) {
							same++;
						} else {
							xEnd = i;
							same = 0;
							lastColor = color;
						}
						if (same >= 128) {
							System.out.println("Resizing Image to " + xEnd);
							if (xEnd > 0)
								img = img.getSubimage(0, 0, xEnd, xEnd);
							break;
						}
					}

					progressBar_3.setValue(progressBar_3.getMaximum());
					g.dispose();
					System.out.println("Writing Map to Disk ...");
					final File mapFile = new File(data_dir.getAbsolutePath() + "/fullMap" + img.getWidth() + ".jpg");
					final File mapFileFile = new File(PathFinder.findDayZToolBoxFolder() + "/mapfile_" + mapFolder.getName() + ".mff");
					if (mapFile.exists())
						mapFile.delete();
					long estimatedSize = estimateSize(data_dir.getAbsolutePath() + "/tempPNGs");
					progressBar_4.setMaximum((int) estimatedSize * 2);
					new Thread(new Runnable() {

						@Override
						public void run() {
							long last = -1;
							while (last != (last = mapFile.length() + mapFileFile.length())) {
								progressBar_4.setValue((int) last);
								if (stop)
									return;
								try {
									Thread.sleep(400);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							progressBar_4.setValue(progressBar_4.getMaximum());
						}
					}).start();
					if (stop)
						return;
//					ImageIO.write(img, "JPEG", mapFile);
//					System.out.println("Finished Writing Map to Disk ...");
//					System.out.println("Map Size:\t" + img.getWidth() + "x" + img.getHeight() + " --> " + (img.getWidth() * img.getHeight()) + " Pixels");
//					System.out.println("Map Space: " + mapFile.length() / 1024 / 1024 + "mb");
//					System.out.println("Tiles: " + xMax + "x" + yMax + " --> " + ((xMax + 1) * (yMax + 1)) + " Tiles");
//					if (stop)
//						return;

					MapFile map = new MapFile(mapFileFile);
					if (mapFileFile.exists())
						map.readPositionsOnly();
					map.removeAllImages();
					map.addMapObject(new MapImage(img));
					map.save();
					Config.getConfig().setString(Constants.CONFIG_LAST_MAP_FILE, mapFileFile.getAbsolutePath());
					progressBar_4.setValue(progressBar_4.getMaximum());

					ErrorDialog.displayInfo("Map successfully created. Open the Map Editor to view it");
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				okButton.setEnabled(true);
			}
		});
		runningThread.start();
	}
	
	private void resetBars() {
		progressBar.setValue(0);
		progressBar_1.setValue(0);
		progressBar_2.setValue(0);
		progressBar_3.setValue(0);
		progressBar_4.setValue(0);
	}
	
	private static int getPendingConversionsCount(File dir) {
		if (dir == null)
			return -1;
		if (!dir.isDirectory())
			return -1;
		int i = 0;
		for (File f2 : dir.listFiles()) {
			if (f2.getName().toLowerCase().startsWith("s_")) {
				i++;
			}
		}
		return i;
	}

	private static boolean hasLayers(File file) throws IOException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(file);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.toLowerCase().contains("layers\\s_000_000_lco.paa")) {
					return true;
				}
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
		return false;
	}

	private static File findLayersFolder(File folder) {
		if (folder.isDirectory()) {
			if (folder.getName().toLowerCase().endsWith("layers")) {
				return folder;
			}
			for (File f : folder.listFiles()) {
				File found = findLayersFolder(f);
				if (found != null)
					return found;
			}
		}
		return null;
	}

	private void displayPBOProcess(final String layersFolder) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File layers = new File(layersFolder);
				int targetFiles = 9200;
				while (displayPBOProcess) {
					if (layers.exists() && layers.isDirectory()) {
						if (stop)
							return;
						progressBar_1.setValue(layers.listFiles().length * progressBar_1.getMaximum() / targetFiles);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
}
