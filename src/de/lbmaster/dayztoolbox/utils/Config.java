package de.lbmaster.dayztoolbox.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {

	private File file;
	private boolean autosave = false;
	private Map<String, String> strings = new HashMap<String, String>();
	private Map<String, Integer> ints = new HashMap<String, Integer>();
	private Map<String, Float> floats = new HashMap<String, Float>();
	private Map<String, Double> doubles = new HashMap<String, Double>();
	private Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	private Map<String, ArrayList<String>> lists = new HashMap<String, ArrayList<String>>();
	private Map<String, Set<String>> sets = new HashMap<String, Set<String>>();

	private static final String SPACER = "_";
	private static final String INDICATOR_STRING = "S" + SPACER;
	private static final String INDICATOR_INT = "I" + SPACER;
	private static final String INDICATOR_FLOAT = "F" + SPACER;
	private static final String INDICATOR_DOUBLE = "D" + SPACER;
	private static final String INDICATOR_BOOLEAN = "B" + SPACER;
	private static final String INDICATOR_LIST = "A" + SPACER;
	private static final String INDICATOR_LIST_ENTRY = "AR" + SPACER;
	private static final String INDICATOR_SET = "SE" + SPACER;
	private static final String INDICATOR_SET_ENTRY = "SER" + SPACER;

	private static Config config;

	public static Config getConfig() {
		if (config == null) {
			new Config(PathFinder.findDayZToolBoxFolder() + "/config.cfg");
			config.read();
		}
		return config;
	}

	public Config(String file) {
		this(new File(file));
	}

	public Config(File file) {
		this.file = file;
		System.out.println("Config with file " + file.getPath() + " created");
		Config.config = this;
	}

	public void setAutoSave(boolean autosave) {
		this.autosave = autosave;
	}

	public boolean isAutoSave() {
		return autosave;
	}

	public boolean hasVariable(String varName) {
		Map<String, Object> all = new HashMap<String, Object>();
		all.putAll(strings);
		all.putAll(ints);
		all.putAll(floats);
		all.putAll(doubles);
		all.putAll(booleans);
		all.putAll(lists);
		all.putAll(sets);
		return all.containsKey(varName);
	}

	public void read() {
		if (file.exists()) {
			clear();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				ArrayList<String> currentList = null;
				Set<String> currentSet = null;
				while (line != null) {
					if (line.startsWith(INDICATOR_STRING)) {
						strings.put(line.substring(INDICATOR_STRING.length(), line.indexOf("=")), line.substring(line.indexOf("=") + 1));
					} else if (line.startsWith(INDICATOR_INT)) {
						ints.put(line.substring(INDICATOR_INT.length(), line.indexOf("=")), Integer.parseInt(line.substring(line.indexOf("=") + 1)));
					} else if (line.startsWith(INDICATOR_FLOAT)) {
						floats.put(line.substring(INDICATOR_FLOAT.length(), line.indexOf("=")), Float.parseFloat(line.substring(line.indexOf("=") + 1)));
					} else if (line.startsWith(INDICATOR_DOUBLE)) {
						doubles.put(line.substring(INDICATOR_DOUBLE.length(), line.indexOf("=")), Double.parseDouble(line.substring(line.indexOf("=") + 1)));
					} else if (line.startsWith(INDICATOR_BOOLEAN)) {
						booleans.put(line.substring(INDICATOR_BOOLEAN.length(), line.indexOf("=")), line.substring(line.indexOf("=") + 1).equalsIgnoreCase("true"));
					} else if (line.startsWith(INDICATOR_LIST)) {
						currentList = new ArrayList<String>();
						lists.put(line.substring(INDICATOR_LIST.length()), currentList);
					} else if (line.startsWith(INDICATOR_LIST_ENTRY)) {
						if (currentList != null) {
							currentList.add(line.substring(INDICATOR_LIST_ENTRY.length()));
						}
					} else if (line.startsWith(INDICATOR_SET)) {
						currentSet = new HashSet<String>();
						sets.put(line.substring(INDICATOR_SET.length()), currentSet);
					} else if (line.startsWith(INDICATOR_SET_ENTRY)) {
						if (currentSet != null) {
							currentSet.add(line.substring(INDICATOR_SET_ENTRY.length()));
						}
					}
					line = br.readLine();
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
		strings.clear();
		ints.clear();
		floats.clear();
		doubles.clear();
		booleans.clear();
		lists.clear();
		sets.clear();
	}

	public void write() {
		try {
			FileWriter fw = new FileWriter(file);
			for (String s : strings.keySet()) {
				fw.write(INDICATOR_STRING + s + "=" + strings.get(s) + "\n");
			}
			for (String s : ints.keySet()) {
				fw.write(INDICATOR_INT + s + "=" + ints.get(s) + "\n");
			}
			for (String s : floats.keySet()) {
				fw.write(INDICATOR_FLOAT + s + "=" + floats.get(s) + "\n");
			}
			for (String s : doubles.keySet()) {
				fw.write(INDICATOR_DOUBLE + s + "=" + doubles.get(s) + "\n");
			}
			for (String s : booleans.keySet()) {
				fw.write(INDICATOR_BOOLEAN + s + "=" + booleans.get(s) + "\n");
			}
			for (String key : lists.keySet()) {
				fw.write(INDICATOR_LIST + key + "\n");
				for (String s : lists.get(key)) {
					fw.write(INDICATOR_LIST_ENTRY + s + "\n");
				}
			}
			for (String key : sets.keySet()) {
				fw.write(INDICATOR_SET + key + "\n");
				for (String s : sets.get(key)) {
					fw.write(INDICATOR_SET_ENTRY + s + "\n");
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setString(String key, String value) {
		if (strings.containsKey(key)) {
			strings.remove(key);
		}
		strings.put(key, value);
		if (autosave)
			write();
	}

	public void setInt(String key, int value) {
		if (ints.containsKey(key)) {
			ints.remove(key);
		}
		ints.put(key, value);
		if (autosave)
			write();
	}

	public void setFloat(String key, float value) {
		if (floats.containsKey(key)) {
			floats.remove(key);
		}
		floats.put(key, value);
		if (autosave)
			write();
	}

	public void setDouble(String key, double value) {
		if (doubles.containsKey(key)) {
			doubles.remove(key);
		}
		doubles.put(key, value);
		if (autosave)
			write();
	}

	public void setBoolean(String key, boolean value) {
		if (booleans.containsKey(key)) {
			booleans.remove(key);
		}
		booleans.put(key, value);
		if (autosave)
			write();
	}

	public void setList(String key, ArrayList<String> value) {
		if (lists.containsKey(key)) {
			lists.remove(key);
		}
		lists.put(key, value);
		if (autosave)
			write();
	}

	public void setSet(String key, Set<String> value) {
		if (sets.containsKey(key)) {
			sets.remove(key);
		}
		sets.put(key, value);
		if (autosave)
			write();
	}

	public String getString(String key) {
		return strings.get(key);
	}

	public int getInt(String key) {
		return ints.get(key);
	}

	public float getFloat(String key) {
		return floats.get(key);
	}

	public double getDouble(String key) {
		return doubles.get(key);
	}

	public boolean getBoolean(String key) {
		return booleans.get(key);
	}

	public ArrayList<String> getList(String key) {
		return lists.get(key);
	}

	public Set<String> getSet(String key) {
		return sets.get(key);
	}

	public String getString(String key, String ret) {
		if (!strings.containsKey(key)) {
			return ret;
		}
		return strings.get(key);
	}

	public int getInt(String key, int ret) {
		if (!ints.containsKey(key)) {
			return ret;
		}
		return ints.get(key);
	}

	public float getFloat(String key, float ret) {
		if (!floats.containsKey(key)) {
			return ret;
		}
		return floats.get(key);
	}

	public double getDouble(String key, double ret) {
		if (!doubles.containsKey(key)) {
			return ret;
		}
		return doubles.get(key);
	}

	public boolean getBoolean(String key, boolean ret) {
		if (!booleans.containsKey(key)) {
			return ret;
		}
		return booleans.get(key);
	}

	public ArrayList<String> getList(String key, ArrayList<String> ret) {
		if (!lists.containsKey(key)) {
			return ret;
		}
		return lists.get(key);
	}

	public Set<String> getSet(String key, Set<String> ret) {
		if (!sets.containsKey(key)) {
			return ret;
		}
		return sets.get(key);
	}

	public void setDefaultString(String key, String value) {
		if (!strings.containsKey(key)) {
			strings.put(key, value);
		}
	}

	public void setDefaultInt(String key, int value) {
		if (!ints.containsKey(key)) {
			ints.put(key, value);
		}
	}

	public void setDefaultDouble(String key, double value) {
		if (!doubles.containsKey(key)) {
			doubles.put(key, value);
		}
	}

	public void setDefaultFloat(String key, float value) {
		if (!floats.containsKey(key)) {
			floats.put(key, value);
		}
	}

	public void setDefaultBoolean(String key, boolean value) {
		if (!booleans.containsKey(key)) {
			booleans.put(key, value);
		}
	}

	public void setDefaultList(String key, ArrayList<String> value) {
		if (!lists.containsKey(key)) {
			lists.put(key, value);
		}
	}

	public void setDefaultSet(String key, Set<String> value) {
		if (!sets.containsKey(key)) {
			sets.put(key, value);
		}
	}
}
