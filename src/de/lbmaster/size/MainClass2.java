package de.lbmaster.size;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainClass2 {

	public static void main(String[] args) {
		File f = new File("src");
		displayFile(f);
		System.out.println(lines + " Lines of Code, " + elines + " Empty Lines and " + imports + " Imports");
		System.out.println("Resulting in " + (elines + lines + imports) + " Lines in " + classes + " Classes, " + enums + " Enums, " + interfaces + " Interfaces and " + files + " .java Files");
	}

	public static int imports = 0;
	public static int lines = 0;
	public static int elines = 0;
	public static int files = 0;
	public static int classes = 0;
	public static int interfaces = 0;
	public static int enums = 0;

	public static void displayFile(File dir) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				displayFile(f);
			}
		} else {
			if (dir.getName().endsWith(".java") && !dir.getName().equals(MainClass2.class.getSimpleName() + ".java")) {
				files++;
				MainClass2.lines += getLines(dir);
			}
		}
	}

	public static int getLines(File f) {
		int lines = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				if (!line.trim().equals("")) {
					if (line.startsWith("import ")) {
						imports++;
					} else {
						lines++;
					}
					if (line.contains(" class ")) {
						classes++;
					} else if (line.contains(" interface ")) {
						interfaces++;
					} else if (line.contains(" enum ")) {
						enums++;
					}
				} else {
					elines++;
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

}
