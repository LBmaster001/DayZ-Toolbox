package de.lbmaster.dayz.configparser;

import java.util.ArrayList;
import java.util.List;

public class ConfigEntryRaw {

	private ConfigEntryType type = ConfigEntryType.UNKNOWN;
	private String text;
	private String varName, varValue;
	private boolean array;
	private List<ConfigEntryRaw> children = new ArrayList<ConfigEntryRaw>();
	private ConfigEntryRaw comment = null;

	private static final ConfigEntryRaw linebreak;

	static {
		linebreak = new ConfigEntryRaw();
		linebreak.varName = "LINEBREAK";
		linebreak.type = ConfigEntryType.LINEBREAK;
	}

	public static List<ConfigEntryRaw> getConfigEntries(String rawInput, boolean addlinebreaks) {
		List<ConfigEntryRaw> entries = new ArrayList<ConfigEntryRaw>();
		ConfigEntryRaw entry = new ConfigEntryRaw();
		entry.text = rawInput;
		while (entry.text.startsWith("\n") || entry.text.startsWith("\r") || entry.text.startsWith(" ")) {
			if (entry.text.startsWith(" ")) {
				entry.text = entry.text.substring(1);
				continue;
			}
			if (addlinebreaks)
				entries.add(linebreak);
			entry.text = entry.text.substring(nextLinebreakLength(entry.text));
		}
		entries.add(entry);
		if (entry.text.startsWith("//")) {
			entry.type = ConfigEntryType.COMMENT;
			entry.text = entry.text.substring(2); // Remove "//"
			if (nextLinebreakIndex(entry.text) >= 0) {
				String copy = entry.text;
				entry.text = copy.substring(0, nextLinebreakIndex(entry.text));
				entries.addAll(getConfigEntries(copy.substring(Math.max(nextLinebreakIndex(copy) + nextLinebreakLength(copy), 0)), addlinebreaks));
			}
			entry.varName = "//";
			entry.varValue = entry.text;
		} else if (entry.text.length() > 4) {
			if (isFirst(entry.text, ";", "class ")) {
				if (entry.text.contains(";")) {
					String copy = entry.text;
					entry.text = copy.substring(0, copy.indexOf(";"));
					copy = copy.substring(copy.indexOf(";") + 1);
					if (isFirst(copy, "//", "\n", ";")) {
						entry.comment = new ConfigEntryRaw();
						entry.comment.varValue = copy.substring(copy.indexOf("//") + 2, nextLinebreakIndex(copy) < 0 ? copy.length() : nextLinebreakIndex(copy));
					}
					copy = copy.substring(Math.max(nextLinebreakIndex(copy) + nextLinebreakLength(copy), 0));
					entries.addAll(getConfigEntries(copy, addlinebreaks));

				}
				if (entry.text.length() > 4) {
					String left = entry.text.substring(0, (entry.text.indexOf("=") == -1 ? entry.text.length() : entry.text.indexOf("="))).trim();
					String right = entry.text.substring(entry.text.indexOf("=") + 1).trim();
					entry.varName = left;
					entry.varValue = right;
					entry.array = left.endsWith("[]");
					if (right.contains("\"")) {
						entry.type = ConfigEntryType.STRING;
						if (!entry.array) {
							entry.varValue = entry.varValue.substring(entry.varValue.indexOf("\"") + 1, entry.varValue.lastIndexOf("\""));
						}

					} else if (right.equals("true") || right.equals("false")) {
						entry.type = ConfigEntryType.BOOLEAN;
					} else if (right.contains(".")) {
						if (right.replace(".", "").replaceAll("[^0-9]", "").length() == right.length()) {
							entry.type = ConfigEntryType.FLOAT;
						} else {
							entry.type = ConfigEntryType.STATIC;
						}
					} else if (right.replaceAll("[^0-9]", "").length() == right.length()) {
						entry.type = ConfigEntryType.INTEGER;
					} else if (right.startsWith("$")) {
						entry.type = ConfigEntryType.REFERENCE;
						if (!entry.array) {
							entry.varValue = entry.varValue.substring(entry.varValue.indexOf("$") + 1);
						}
					}
				}
			} else if (isFirst(entry.text, "class ", ";", "{", "}")) {
				entry.type = ConfigEntryType.CLASS;
				int classEnd = findClassEnd(entry.text);
				entry.children = getConfigEntries(entry.text.substring(entry.text.indexOf("{") + 1, classEnd), false);
				removeNull(entry.children);
				entries.addAll(getConfigEntries(entry.text.substring(classEnd < entry.text.length() ? classEnd + 1 : classEnd), false));
				entry.text = entry.text.substring(0, entry.text.indexOf("{"));
				entry.varName = entry.text.substring(entry.text.indexOf("class ") + 6);
			}
		}
		removeNull(entries);
		return entries;
	}

	private static int nextLinebreakIndex(String s) {
		int backNIndex = s.indexOf("\n");
		int backRIndex = s.indexOf("\r");
		if (backNIndex == -1)
			return backRIndex;
		if (backRIndex == -1)
			return backNIndex;
		return Math.min(backNIndex, backRIndex);
	}

	private static int nextLinebreakLength(String s) {
		int index = nextLinebreakIndex(s);
		if (index >= 0) {
			String s2 = s.substring(index);
			if (s2.startsWith("\r\n"))
				return 2;
			if (s2.startsWith("\r") || s2.startsWith("\n"))
				return 1;
		}
		return 0;
	}

	private static void removeNull(List<ConfigEntryRaw> entries) {
		List<ConfigEntryRaw> copy = new ArrayList<ConfigEntryRaw>();
		copy.addAll(entries);
		for (ConfigEntryRaw entry : copy)
			if (entry.getVarName() == null)
				entries.remove(entry);
	}

	private static int findClassEnd(String text) {
		int open = 0;
		int index = 0;
		for (char c : text.toCharArray()) {
			if (c == '}') {
				open--;
				if (open <= 0)
					return index;
			} else if (c == '{') {
				open++;
			}
			index++;
		}
		return index;
	}

	private static boolean isFirst(String text, String first, String... strings) {
		int lowestIndex = text.length();
		for (String s : strings) {
			int index = text.indexOf(s);
			if (index >= 0 && index < lowestIndex)
				lowestIndex = index;
		}
		return text.indexOf(first) >= 0 && text.indexOf(first) <= lowestIndex;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public List<ConfigEntryRaw> getChildrens() {
		return children;
	}
	
	public ConfigEntryRaw getChildren(String key, boolean casesensitive) {
		if (!hasChildren())
			return null;
		for (ConfigEntryRaw entry : getChildrens()) {
			if (casesensitive ? entry.getVarName().trim().equals(key) : entry.getVarName().trim().equalsIgnoreCase(key))
				return entry;
		}
		if (key.equals("")) {
			return getChildrens().get(0);
		}
		return null;
	}

	public String getText() {
		return text;
	}

	public ConfigEntryType getType() {
		return type;
	}

	public String getVarName() {
		return varName;
	}

	public String getVarValue() {
		return varValue;
	}

	public boolean hasComment() {
		return comment != null;
	}

	public ConfigEntryRaw getCommentRaw() {
		return comment;
	}

	public String getComment() {
		if (hasComment())
			return getCommentRaw().getVarValue();
		return null;
	}

	public void setComment(String comment) {
		this.comment = new ConfigEntryRaw();
		this.comment.setVarValue(comment);
	}

	public void removeComment() {
		this.comment = null;
	}

	public boolean isArray() {
		return array;
	}

	public void setVarValue(String value) {
		this.varValue = value;
	}

	public void setVarName(String name) {
		this.varName = name;
	}

	public void setType(ConfigEntryType type) {
		this.type = type;
	}

	public void setArray(boolean array) {
		this.array = array;
	}
}
