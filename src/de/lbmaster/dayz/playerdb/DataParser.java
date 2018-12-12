package de.lbmaster.dayz.playerdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.lbmaster.dayztoolbox.utils.ByteUtilsBE;

public class DataParser {
	
	public List<DBPlayer> loadAllDBPlayers(Map<String, byte[]> data) {
		List<DBPlayer> list = new ArrayList<DBPlayer>();
		
		for (Entry<String, byte[]> entry : data.entrySet()) {
			list.add(loadDBPlayer(entry.getValue(), entry.getKey()));
		}
		
		return list;
	}
	
	public DBPlayer loadDBPlayer(byte[] content, String guid) {
		System.out.println("Playerdata of Player " + guid + " : " + ByteUtilsBE.bytesToHex(content));
		DBPlayer player = new DBPlayer(guid);
		Data data = new Data(content);
		String version = data.getHexString(4);
		// System.out.println("Version: " + version);
		int charID = data.getInt();
		int dbID = data.getInt();
		player.setIds(charID, dbID);
		data.skipBytes(4); // Unknown bytes
		float x = data.getFloat();
		float y = data.getFloat();
		float z = data.getFloat();
		player.setPosition(x, y, z);
		data.skipBytes(2);
		String modelname = data.getString();
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
