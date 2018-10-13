package de.lbmaster.dayztoolbox.guis.economyconfiggui;

import javax.swing.JPanel;

public class CheckBoxGrid {
	
	private CheckBoxRow[] rows = new CheckBoxRow[8];
	private String[] entries = new String[] {"dynamic","animals","zombies","vehicles","randoms","custom","building","player"};
	
	public CheckBoxGrid(String xml) {
		int index = 0;
		for (String s : entries) {
			CheckBoxRow row = new CheckBoxRow(s);
			rows[index++] = row;
		}
		loadXML(xml);
	}
	
	public void loadXML(String xml) {
		for (int index = 0; index < entries.length; index++) {
			rows[index].setSelections(xml);
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		for (CheckBoxRow row : rows) {
			s += "\t" + row.toString() + "\n";
		}
		return "<economy>\n" + s + "</economy>";
	}
	
	public String toXml() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + toString();
	}
	
	public void addToPanel(JPanel panel, int rowStart, int colStart) {
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < rows[0].getItems().length; j++) {
				panel.add(rows[i].getItems()[j], (rowStart+j) + ", " + (colStart+i));
			}
		}
	}
	
	public void removeFromPanel(JPanel panel) {
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < rows[0].getItems().length; j++) {
				panel.remove(rows[i].getItems()[j]);
			}
		}
	}
	
	public CheckBoxRow getRow(int index) {
		return rows[index];
	}

}
