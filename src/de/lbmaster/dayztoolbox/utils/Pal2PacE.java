package de.lbmaster.dayztoolbox.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import de.lbmaster.dayztoolbox.Constants;

public class Pal2PacE {
	private String pal2PacEDir;
	
	private Pal2PacE(String pal2PacEDir) {
		this.pal2PacEDir = pal2PacEDir;
	}

	public static Pal2PacE findPal2PacE() {
		String pal2pac = Config.getConfig().getString(Constants.CONFIG_pal2pace);
		if (pal2pac == null)
			return null;
		File location = new File(pal2pac);
		if (!location.exists() || !location.isDirectory()) {
			return null;
		}
		return new Pal2PacE(location.getAbsolutePath());
	}
	
	public String getPal2PacELocation() {
		return pal2PacEDir;
	}
	
	public void paaToPng(String paaFile, String outputFile) {
		try {
//			System.out.println("Converting Paa to PNG File:  " + paaFile);
			System.out.println("Executing command " + "\"" + pal2PacEDir.replace("/", "\\") + "\\Pal2PacE.exe\" \"" + paaFile.replace("/", "\\") + "\" \"" + outputFile.replace("/", "\\") + "\"");
			Process p = Runtime.getRuntime().exec("\"" + pal2PacEDir.replace("/", "\\") + "\\Pal2PacE.exe\" \"" + paaFile.replace("/", "\\") + "\" \"" + outputFile.replace("/", "\\") + "\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null)
				sb.append(line + "\n");
			p.waitFor();
//			System.out.println("Converted Paa to PNG File:  " + outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
