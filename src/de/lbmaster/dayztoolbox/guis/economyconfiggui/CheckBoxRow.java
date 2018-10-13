package de.lbmaster.dayztoolbox.guis.economyconfiggui;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class CheckBoxRow {
	
	private JCheckBox[] items = new JCheckBox[4];
	private final String xmlName;
	
	public CheckBoxRow(String xmlName) {
		this.xmlName = xmlName;
	}
	
	public JCheckBox[] getItems() {
		return items;
	}
	
	@Override
	public String toString() {
		return "<" + xmlName + " init=\"" + (items[0].isSelected() ? 1 : 0) + "\" load=\"" + (items[1].isSelected() ? 1 : 0) + "\" respawn=\"" + (items[2].isSelected() ? 1 : 0) + "\" save=\"" + (items[3].isSelected() ? 1 : 0) + "\"/>";
	}
	
	public void setSelections(String xml) {
		Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
		Elements elm = doc.getElementsByTag("economy").get(0).getAllElements();
		int index = 0;
		for (int i = 1; i < elm.size(); i++) {
			Element e = elm.get(i).getAllElements().get(0);
			if (e.tagName().toLowerCase().equals(xmlName.toLowerCase())) {
				JCheckBox box = items[index] == null ? new JCheckBox() : items[index];
				box.setHorizontalAlignment(SwingConstants.CENTER);
				box.setSelected(e.hasAttr("init") ?  e.attr("init").equals("1") : false);
				items[index++] = box;
				box = items[index] == null ? new JCheckBox() : items[index];
				box.setHorizontalAlignment(SwingConstants.CENTER);
				box.setSelected(e.hasAttr("load") ? e.attr("load").equals("1") : false);
				items[index++] = box;
				box = items[index] == null ? new JCheckBox() : items[index];
				box.setHorizontalAlignment(SwingConstants.CENTER);
				box.setSelected(e.hasAttr("respawn") ? e.attr("respawn").equals("1") : false);
				items[index++] = box;
				box = items[index] == null ? new JCheckBox() : items[index];
				box.setHorizontalAlignment(SwingConstants.CENTER);
				box.setSelected(e.hasAttr("save") ? e.attr("save").equals("1") : false);
				items[index++] = box;
			}
		}
	}
}
