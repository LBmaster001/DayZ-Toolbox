package de.lbmaster.dayztoolbox.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PBOManager {

	private String pboManagerDir;
	
	public PBOManager(String pboManagerDir) {
		this.pboManagerDir = pboManagerDir;
	}
	
	public String getPBOManagerLocation() {
		return pboManagerDir;
	}

	public void packPBO(String folder) {
		try {
			System.out.println("Packing " + folder);
			Process p = Runtime.getRuntime().exec(pboManagerDir.replace("/", "\\") + "\\PBOConsole.exe -pack \"" + folder + "\" \"" + folder + ".pbo\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null)
				sb.append(line + "\n");
			p.waitFor();
			System.out.println("Packed " + folder);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void exctract(String pbo) {
		try {
			String pboFolder = pbo;
			if (new File(pbo + "_original.pbo").exists()) {
				pbo += "_original";
				System.out.println("Found " + pbo + ". Using the Original file instead.");
			}
			System.out.println("Extracting " + pbo + ".pbo");
			System.out.println("Executing command: " + pboManagerDir.replace("/", "\\") + "\\PBOConsole.exe -unpack \"" + pbo + ".pbo\" \"" + pboFolder + "\"");
			Process p = Runtime.getRuntime().exec("\"" + pboManagerDir.replace("/", "\\") + "\\PBOConsole.exe\" -unpack \"" + pbo + ".pbo\" \"" + pboFolder + "\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null)
				sb.append(line + "\n");
			p.waitFor();
			System.out.println("Extracted " + pbo);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
