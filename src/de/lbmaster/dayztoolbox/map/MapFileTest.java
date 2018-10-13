package de.lbmaster.dayztoolbox.map;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapFileTest {

	public static void main(String[] args) throws IOException {
		File mffFile = new File("mapfile.mff");
		
		MapFile map = new MapFile(mffFile);
		map.readContent();
		map.removeAllPositions();
		MapPositions positions = new MapPositions();
		positions.loadFromPlayerPositionsFile(new File("D:\\SteamGames\\steamapps\\common\\DayZServer\\mpmissions\\dayzOffline.chernarusplus\\cfgPlayerSpawnPoints.xml"));
		for (MapPosition pos : positions.getPositions()) {
			System.out.println(pos.getName() + " " + pos.getX() + " " + pos.getZ());
		}
		map.addMapObject(positions);
		List<MapPositions> events = MapPositions.loadEventPositions(new File("D:\\SteamGames\\steamapps\\common\\DayZServer\\mpmissions\\dayzOffline.chernarusplus\\cfgEventSpawns.xml"));
		for (MapPositions pos : events)  {
			map.addMapObject(pos);
		}
		System.out.println("Contents: " + map.getContent().size());
		map.save();
	}
}
