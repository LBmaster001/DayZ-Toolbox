package de.lbmaster.dayz.playerdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataParser {
	
	public List<DBPlayer> loadAllDBPlayers(Map<String, byte[]> data) {
		List<DBPlayer> list = new ArrayList<DBPlayer>();
		
		for (Entry<String, byte[]> entry : data.entrySet()) {
			list.add(loadDBPlayer(entry.getValue(), entry.getKey()));
		}
		
		return list;
	}
	
	public DBPlayer loadDBPlayer(byte[] content, String guid) {
		DBPlayer player = new DBPlayer(guid);
		Data data = new Data(content);
		String version = data.getHexString(4);
		// System.out.println("Version: " + version);
		int charID = data.getInt();
		int dbID = data.getInt();
		player.setIds(charID, dbID);
		Data positionData = data.getData();
		float x = positionData.getFloat();
		float y = positionData.getFloat();
		float z = positionData.getFloat();
		player.setPosition(x, y, z);
		positionData.skipBytes(2);
		int stringLength = positionData.getByte();
		String modelname = data.getString(stringLength);
		player.setModel(modelname);
		// System.out.println(modelname);
		Data statsData = data.getData();
		Map<String, Float> stats = new HashMap<String, Float>();
		byte statsCount = statsData.getByte();
		// System.out.println("StatsCount: " + statsCount);
		for (byte b = 0; b < statsCount; b++) {
			String s = statsData.getString();
			float f = statsData.getFloat();
			// System.out.println("Stat " + s + " = " + f);
			stats.put(s, f);
		}
		player.addStats(stats);
		Data charData = data.getData();
		int itemcount = data.getInt();
		System.out.println("Root Item Count: " + itemcount);
		for (int i = 0; i < itemcount; i++) {
			Item item = new Item();
			item.loadFromData(data.getData());
			player.addItemToRoot(item);
		}
		// System.out.println("Itemcount: " + itemcount);
		return player;
	}

}
