package de.lbmaster.dayz.configparser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DayZConfig {

	private final File file;
	private static final int maxTabs = 9;

	private String content;
	private boolean read = false;
	private boolean defaultcasesensitive = false;

	private List<ConfigEntryRaw> entries = new ArrayList<ConfigEntryRaw>();

	private List<ConfigEntryRaw> strings = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> integers = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> floats = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> booleans = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> references = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> classes = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> comments = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> statics = new ArrayList<ConfigEntryRaw>();
	private List<ConfigEntryRaw> unknowns = new ArrayList<ConfigEntryRaw>();

	public DayZConfig(String path) {
		this.file = new File(path);
	}

	public DayZConfig(File file) {
		this.file = file;
	}

	public boolean canWrite() {
		if (file.canWrite()) {
			try {
				FileOutputStream fw = new FileOutputStream(file);
				fw.close();
				return true;
			} catch (Exception e) {
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

	protected boolean readFromContent(String content) {
		this.content = content;
		this.entries.clear();
		this.entries = ConfigEntryRaw.getConfigEntries(this.content, true);
		sortEntries();
		this.read = true;
		return true;
	}

	public boolean save() throws IOException {
		return save(this.file);
	}

	public boolean saveIgnore() {
		try {
			return save(this.file);
		} catch (IOException e) {
		}
		return false;
	}

	public boolean save(File file) throws IOException {
		if (file == null || file.exists() ? (!file.canWrite() || !file.isFile()) : false)
			return false;
//		System.out.println("OK (1/3) !");
		if (!file.exists() && !createFile(file))
			return false;
//		System.out.println("OK (2/3) !");
		FileWriter fw = setUpWriter(file);
		if (fw == null)
			return false;
//		System.out.println("OK (3/3) !");
		// displayEntries(entries);
		for (ConfigEntryRaw entry : entries) {
			fw.write(generateEntryString(entry, ""));
		}
		fw.flush();
		fw.close();
		return true;
	}

	public void displayEntries(List<ConfigEntryRaw> list) {
		for (ConfigEntryRaw entry : list) {
			System.out.println(entry.getType().toString() + " " + entry.getVarName());
		}
	}

	public String getFileLocation() {
		return file.getAbsolutePath();
	}

	public File getFile() {
		return file;
	}

	private String generateEntryString(ConfigEntryRaw entry, String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		switch (entry.getType()) {
		case COMMENT:
			sb.append("// " + entry.getVarValue());
			break;
		case BOOLEAN:
		case FLOAT:
		case INTEGER:
		case REFERENCE:
		case STATIC:
		case UNKNOWN:
			sb.append(entry.getVarName() + " = " + entry.getVarValue() + ";" + (entry.hasComment() ? addTabs(entry.getVarName() + " = " + entry.getVarValue() + ";") + "//" + entry.getComment() : ""));
			break;
		case STRING:
			if (!entry.isArray())
				sb.append(entry.getVarName() + " = \"" + entry.getVarValue() + "\";" + (entry.hasComment() ? addTabs(entry.getVarName() + " = \"" + entry.getVarValue() + "\";") + "//" + entry.getComment() : ""));
			else
				sb.append(entry.getVarName() + " = " + entry.getVarValue() + ";" + (entry.hasComment() ? addTabs(entry.getVarName() + " = " + entry.getVarValue() + ";") + "//" + entry.getComment() : ""));
			break;
		case CLASS:
			if (entry.hasChildren()) {
				sb.append("class " + entry.getVarName().trim() + "\n" + prefix + "{");
				sb.append("\n" + generateChildrenTree(entry, prefix) + prefix);
			} else {
				sb.append("class " + entry.getVarName().trim() + " {");
			}
			sb.append("};");
			break;
		case LINEBREAK:
			sb.append("\n");
			break;
		default:
			break;
		}
		if (entry.getType() != ConfigEntryType.LINEBREAK) {
			sb.append("\n");
		}
		return sb.toString();
	}

	private String addTabs(String before) {
		int tabs = before.length() / 4;
		int add = Math.max(maxTabs - tabs, 1);
		String s = "";
		for (int i = 0; i < add; i++)
			s += "\t";
		return s;
	}

	private String generateChildrenTree(ConfigEntryRaw entry, String prefix) {
		StringBuilder sb = new StringBuilder();
		for (ConfigEntryRaw child : entry.getChildrens()) {
			sb.append(generateEntryString(child, prefix + "\t"));
		}
		return sb.toString();
	}

	private FileWriter setUpWriter(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		return fw;
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

	private void sortEntries() {
		strings.clear();
		integers.clear();
		floats.clear();
		booleans.clear();
		references.clear();
		classes.clear();
		comments.clear();
		statics.clear();
		unknowns.clear();
		for (ConfigEntryRaw entry : entries) {
			switch (entry.getType()) {
			case BOOLEAN:
				booleans.add(entry);
				break;
			case CLASS:
				classes.add(entry);
				break;
			case COMMENT:
				comments.add(entry);
				break;
			case FLOAT:
				floats.add(entry);
				break;
			case INTEGER:
				integers.add(entry);
				break;
			case REFERENCE:
				references.add(entry);
				break;
			case STATIC:
				statics.add(entry);
				break;
			case STRING:
				strings.add(entry);
				break;
			case UNKNOWN:
				unknowns.add(entry);
				break;
			default:
				break;
			}
		}
	}

	public void setDefaultCaseSensitive(boolean b) {
		this.defaultcasesensitive = b;
	}

	public boolean isDefaultcasesensitive() {
		return this.defaultcasesensitive;
	}

	public ConfigEntryRaw getEntryRawChildren(String key, boolean casesensitive, List<ConfigEntryRaw> entries) {
		String[] keys = key.split("\\.");
		ConfigEntryRaw entry = getEntryRaw(keys[0], casesensitive, this.entries);
		for (int i = 1; i < keys.length; i++) {
			if (entry == null)
				break;
			entry = entry.getChildren(keys[i], casesensitive);
			System.out.println("Child!" + keys[i] + " null ? " + (entry == null));
		}
		return entry;
	}

	public ConfigEntryRaw getEntryRaw(String key, boolean casesensitive, List<ConfigEntryRaw> entries) {
		for (ConfigEntryRaw entry : entries) {
			if (casesensitive ? entry.getVarName().trim().equals(key) : entry.getVarName().trim().equalsIgnoreCase(key)) {
				return entry;
			}
		}
		return null;
	}

	// STRINGS
	public List<ConfigEntryRaw> getStrings() {
		return strings;
	}

	public String getStirng(String key) {
		return getString(key, isDefaultcasesensitive());
	}

	public String getString(String key, String elseReturn) {
		return getString(key, isDefaultcasesensitive(), elseReturn);
	}

	public String getString(String key, boolean casesensitive) {
		return getString(key, casesensitive, null);
	}

	public String getString(String key, boolean casesensitive, String elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, strings);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	public void setString(String key, String value) {
		setString(key, isDefaultcasesensitive(), value);
	}

	public void setString(String key, boolean casesensitive, String value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, strings);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			strings.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.STRING);
			entry.setVarName(key);
		}
		entry.setVarValue(value);
	}

	// INTEGERS
	public List<ConfigEntryRaw> getIntegers() {
		return integers;
	}

	public int getInteger(String key) {
		return getInteger(key, isDefaultcasesensitive());
	}

	public int getInteger(String key, int elseReturn) {
		return getInteger(key, isDefaultcasesensitive(), elseReturn);
	}

	public int getInteger(String key, boolean casesensitive) {
		return getInteger(key, casesensitive, -1);
	}

	public int getInteger(String key, boolean casesensitive, int elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, integers);
		if (entry == null)
			return elseReturn;
		return Integer.parseInt(entry.getVarValue());
	}

	public void setInteger(String key, int value) {
		setInteger(key, isDefaultcasesensitive(), value);
	}

	public void setInteger(String key, boolean casesensitive, int value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, integers);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			integers.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.INTEGER);
			entry.setVarName(key);
		}
		entry.setVarValue("" + value);
	}

	// FLOATS
	public List<ConfigEntryRaw> getFloats() {
		return floats;
	}

	public float getFloat(String key) {
		return getFloat(key, isDefaultcasesensitive());
	}

	public float getFloat(String key, float elseReturn) {
		return getFloat(key, isDefaultcasesensitive(), elseReturn);
	}

	public float getFloat(String key, boolean casesensitive) {
		return getFloat(key, casesensitive, -1);
	}

	public float getFloat(String key, boolean casesensitive, float elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, floats);
		if (entry == null)
			return elseReturn;
		return Float.parseFloat(entry.getVarValue());
	}

	public void setFloat(String key, float value) {
		setFloat(key, isDefaultcasesensitive(), value);
	}

	public void setFloat(String key, boolean casesensitive, float value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, floats);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			floats.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.FLOAT);
			entry.setVarName(key);
		}
		entry.setVarValue("" + value);
	}

	// BOOLEANS
	public List<ConfigEntryRaw> getBooleans() {
		return booleans;
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, isDefaultcasesensitive());
	}

	public boolean getBoolean(String key, boolean casesensitive) {
		return getBoolean(key, casesensitive, false);
	}

	public boolean getBoolean(String key, boolean casesensitive, boolean elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, booleans);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue().equals("true");
	}

	public void setBoolean(String key, boolean value) {
		setBoolean(key, isDefaultcasesensitive(), value);
	}

	public void setBoolean(String key, boolean casesensitive, boolean value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, booleans);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			booleans.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.BOOLEAN);
			entry.setVarName(key);
		}
		entry.setVarValue("" + value);
	}

	// REFERENCES
	public List<ConfigEntryRaw> getReferences() {
		return references;
	}

	public String getReference(String key) {
		return getReference(key, isDefaultcasesensitive());
	}

	public String getReference(String key, String elseReturn) {
		return getReference(key, isDefaultcasesensitive(), elseReturn);
	}

	public String getReference(String key, boolean casesensitive) {
		return getReference(key, casesensitive, null);
	}

	public String getReference(String key, boolean casesensitive, String elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, references);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	public void setReference(String key, String value) {
		setReference(key, isDefaultcasesensitive(), value);
	}

	public void setReference(String key, boolean casesensitive, String value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, references);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			references.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.REFERENCE);
			entry.setVarName(key);
		}
		entry.setVarValue("$" + value);
	}

	// CLASSES
	public List<ConfigEntryRaw> getClasses() {
		return classes;
	}

	public String getClass(String key) {
		return getClass(key, isDefaultcasesensitive());
	}

	public String getClass(String key, String elseReturn) {
		return getClass(key, isDefaultcasesensitive(), elseReturn);
	}

	public String getClass(String key, boolean casesensitive) {
		return getClass(key, casesensitive, null);
	}

	public String getClass(String key, boolean casesensitive, String elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, classes);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	public void setClass(String key, String value) {
		setClass(key, isDefaultcasesensitive(), value);
	}

	public void setClass(String key, boolean casesensitive, String value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, classes);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			classes.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.CLASS);
			entry.setVarName(key);
		}
		entry.setVarValue(value);
	}

	// COMMENTS
	public List<ConfigEntryRaw> getComments() {
		return comments;
	}

	public ConfigEntryRaw getCommentEntryRaw(int index) {
		if (comments.size() >= index)
			return comments.get(index);
		return null;
	}

	public String getComment(int index) {
		return getComment(index, null);
	}

	public String getComment(int index, String elseReturn) {
		ConfigEntryRaw entry = getCommentEntryRaw(index);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	// STATICS
	public List<ConfigEntryRaw> getStatics() {
		return statics;
	}

	public String getStatic(String key) {
		return getStatic(key, isDefaultcasesensitive());
	}

	public String getStatic(String key, String elseReturn) {
		return getStatic(key, isDefaultcasesensitive(), elseReturn);
	}

	public String getStatic(String key, boolean casesensitive) {
		return getStatic(key, casesensitive, null);
	}

	public String getStatic(String key, boolean casesensitive, String elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, statics);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	public void setStatic(String key, String value) {
		setStatic(key, isDefaultcasesensitive(), value);
	}

	public void setStatic(String key, boolean casesensitive, String value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, statics);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			statics.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.STATIC);
			entry.setVarName(key);
		}
		entry.setVarValue(value);
	}

	// UNKNOWNS
	public List<ConfigEntryRaw> getUnknowns() {
		return unknowns;
	}

	public String getUnknown(String key) {
		return getUnknown(key, isDefaultcasesensitive());
	}

	public String getUnknown(String key, String elseReturn) {
		return getUnknown(key, isDefaultcasesensitive(), elseReturn);
	}

	public String getUnknown(String key, boolean casesensitive) {
		return getUnknown(key, casesensitive, null);
	}

	public String getUnknown(String key, boolean casesensitive, String elseReturn) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, unknowns);
		if (entry == null)
			return elseReturn;
		return entry.getVarValue();
	}

	public void setUnknown(String key, String value) {
		setUnknown(key, isDefaultcasesensitive(), value);
	}

	public void setUnknown(String key, boolean casesensitive, String value) {
		ConfigEntryRaw entry = getEntryRawChildren(key, casesensitive, unknowns);
		if (entry == null) {
			entry = new ConfigEntryRaw();
			unknowns.add(entry);
			entries.add(entry);
			entry.setType(ConfigEntryType.UNKNOWN);
			entry.setVarName(key);
		}
		entry.setVarValue(value);
	}
}
