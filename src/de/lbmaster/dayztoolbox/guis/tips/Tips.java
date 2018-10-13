package de.lbmaster.dayztoolbox.guis.tips;

import java.util.HashMap;
import java.util.Map;

public class Tips {

	public static final Map<String, String> tips = new HashMap<String, String>();
	
	static {
		tips.put("Downloads", "PBOManager download: <a href=\"http://www.armaholic.com/page.php?id=16369\">download</a><br>"
				+ "Pal2PacE download (part of the Package): <a href=\"http://www.armaholic.com/page.php?id=14435\">download</a>");
		tips.put("Install DayZServer", "1. Open Steam\n2. Navigate to 'Library' -> 'Tools'\n3. Find 'DayZ Server'\n4. Rightclick it and press 'install Game' and follow Steams further instructions");
	}
	
}
