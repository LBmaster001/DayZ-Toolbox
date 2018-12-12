package de.lbmaster.dayz.configparser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BattleyeConfig {
	
	private File file;
	private boolean read = false;
	
	private Map<String, String> entries = new HashMap<String, String>();
	
	public BattleyeConfig(String path) {
		this.file = new File(path);
	}

	public BattleyeConfig(File file) {
		this.file = file;
	}
	
	public boolean canWrite() {
		if (file.canWrite()) {
			try {
				FileOutputStream fw = new FileOutputStream(file);
				fw.close();
				return true;
			} catch(Exception e) {
				return false;
			}
		}
		return false;
	}
	
	public boolean isRead() {
		return this.read;
	}

	public boolean read() throws IOException {
		this.read = false;
		if (!file.exists() || !file.canRead() || !file.isFile())
			return false;
		byte[] content = Files.readAllBytes(file.toPath());
		return (readFromContent(new String(content)));
	}

	private boolean readFromContent(String content) {
		for (String line : content.split("\n")) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			String key = "";
			String value = "";
			if (line.contains(" ")) {
				key = line.substring(0, line.indexOf(" "));
				value = line.substring(line.indexOf(" ")+1);
			} else {
				key = line;
			}
			entries.put(key, value);
		}
		return true;
	}
	
	public boolean save() throws IOException {
		return save(this.file);
	}

	public boolean save(File file) throws IOException {
		if (file == null || file.exists() ? (!file.canWrite() || !file.isFile()) : false)
			return false;
		System.out.println("OK (1/3) !");
		if (!file.exists() && !createFile(file))
			return false;
		System.out.println("OK (2/3) !");
		FileWriter fw = setUpWriter(file);
		if (fw == null)
			return false;
		System.out.println("OK (3/3) !");
//		displayEntries(entries);
		for (Entry<String, String> entry : entries.entrySet()) {
			fw.write(entry.getKey() + (entry.getValue().length() == 0 ? "" : " " + entry.getValue()) + "\r\n");
		}
		fw.flush();
		fw.close();
		return true;
	}

	public String getFileLocation() {
		return file.getAbsolutePath();
	}
	
	public File getFile() {
		return file;
	}
	
	private FileWriter setUpWriter(File file) {
		try {
			FileWriter fw = new FileWriter(file);
			return fw;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean createFile(File file) {
		file.getParentFile().mkdirs();
		try {
			if (!file.createNewFile()) {
				System.out.println("Failed to create File");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getString(String key, String elseReturn) {
		if (!entries.containsKey(key)) {
			return elseReturn;
		}
		return entries.get(key);
	}
	
	public int getInt(String key, int elseReturn) {
		if (!entries.containsKey(key)) {
			return elseReturn;
		}
		return Integer.parseInt(entries.get(key));
	}
	
	public void setString(String key, String value) {
		if (entries.containsKey(key)) {
			entries.remove(key);
		}
		entries.put(key, value);
	}
	
	public void setInt(String key, int value) {
		if (entries.containsKey(key)) {
			entries.remove(key);
		}
		entries.put(key, value + "");
	}

}
