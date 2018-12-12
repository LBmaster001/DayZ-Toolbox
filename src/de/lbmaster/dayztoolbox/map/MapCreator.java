package de.lbmaster.dayztoolbox.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PBOManager;
import de.lbmaster.dayztoolbox.utils.Pal2PacE;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class MapCreator {

	public static void main(String[] args) {
		new MapCreator("B:\\SteamGames\\steamapps\\common\\DayZ\\DayZ_x64.exe");
	}

	public MapCreator(String dayzFolder) {
		File f = new File(dayzFolder);
		if (!f.exists())
			return;
		if (f.isFile()) {
			f = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - f.getName().length()));
		}
		if (!f.isDirectory())
			return;
		System.out.println("OK " + f.getAbsolutePath());
		String path = f.getAbsolutePath();
		if (path.endsWith("DayZ")) {
			path += "/Addons";
		}
		f = new File(path);
		File data_dir = new File(path + "/worlds_chernarusplus_data");
		if (!data_dir.exists()) {
			String pboManagerDir = Config.getConfig().getString(Constants.CONFIG_LOCATION_PBOMANAGER, null);
			if (pboManagerDir == null) {
				List<String> possiblePBOManagers = PathFinder.getPossiblePBOManagerLocations();
				if (possiblePBOManagers.size() > 0) {
					pboManagerDir = possiblePBOManagers.get(0);
				} else {
					System.out.println("No PBO Manager found !");
					return;
				}
			}
			PBOManager pboManager = new PBOManager(pboManagerDir);
			System.out.println("PBO Manager found ! " + pboManager.getPBOManagerLocation());

			pboManager.exctract(data_dir.getAbsolutePath() + ".pbo");
		} else {
			System.out.println("WorldData Dir already exsists");
		}
		if (!data_dir.exists() || !data_dir.isDirectory()) {
			System.out.println("Something went wrong while extracting the PBO");
			return;
		}
		Pal2PacE pal2pace = Pal2PacE.findPal2PacE();
		if (pal2pace == null) {
			System.out.println("No Pal2PacE.exe found ! Cannot convert PAA Files to PNG !");
			return;
		} else {
			System.out.println("Pal2PacE.exe found ! " + pal2pace.getPal2PacELocation());
		}

		data_dir = new File(data_dir.getAbsolutePath() + "/layers");
		if (!data_dir.exists()) {
			System.out.println("Malformed Word Data Folder ! No Layers Folder found !");
			return;
		}
		int xMax = 0;
		int yMax = 0;
		File pngOutput = new File(data_dir.getAbsolutePath() + "/tempPNGs");
		if (!pngOutput.exists()) {
			if (!pngOutput.mkdirs() || !pngOutput.isDirectory()) {
				System.out.println("Temp Directory creation Error !");
				return;
			}
		}
		for (File f2 : data_dir.listFiles()) {
			if (f2.getName().startsWith("m_")) {
				String[] name = f2.getName().split("_");
				int x = Integer.parseInt(name[1]);
				int y = Integer.parseInt(name[2]);
				if (x > xMax)
					xMax = x;
				if (y > yMax)
					yMax = y;
				String outputFile = data_dir.getAbsolutePath() + "/tempPNGs/" + f2.getName().substring(0, f2.getName().lastIndexOf(".")) + ".png";
				if (new File(outputFile).exists())
					continue;
				System.out.println(outputFile);
				pal2pace.paaToPng(f2.getAbsolutePath(), outputFile);
			}
		}
		BufferedImage firstImg = null;
		try {
			firstImg = ImageIO.read(pngOutput.listFiles()[0]);
			int correctionPixelsOff = 16;
			int imageWidth = firstImg.getWidth() - correctionPixelsOff * 2;
			int imageHeight = firstImg.getHeight() - correctionPixelsOff * 2;
			BufferedImage img = new BufferedImage(imageWidth * (xMax + 1), imageHeight * (yMax + 1), firstImg.getType());
			Graphics g = img.createGraphics();
			for (File image : pngOutput.listFiles()) {
				System.out.println("Processing " + image.getName());
				String[] name = image.getName().split("_");
				int x = Integer.parseInt(name[1]);
				int y = Integer.parseInt(name[2]);
				BufferedImage imgAdd = ImageIO.read(image);
				g.drawImage(imgAdd, x * imageWidth - correctionPixelsOff, y * imageHeight - correctionPixelsOff, null);
			}
			g.dispose();
			System.out.println("Writing Map to Disk ...");
			File mapFile = new File(data_dir.getAbsolutePath() + "/fullMap" + img.getWidth() + ".png");
			ImageIO.write(img, "PNG", mapFile);
			System.out.println("Finished Writing Map to Disk ...");
			System.out.println("Map Size:\t" + img.getWidth() + "x" + img.getHeight() + " --> " + (img.getWidth() * img.getHeight()) + " Pixels");
			System.out.println("Map Space: " + mapFile.length() / 1024 / 1024 + "mb");
			System.out.println("Tiles: " + xMax + "x" + yMax + " --> " + ((xMax + 1) * (yMax + 1)) + " Tiles");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
