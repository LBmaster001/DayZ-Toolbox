package de.lbmaster.dayz.configparser;

import java.io.File;

public class DefaultDayZConfig extends DayZConfig {

	public DefaultDayZConfig(String file) {
		this(new File(file));
	}
	
	public DefaultDayZConfig(File file) {
		super(file);
		readFromContent(getDefaultConfigContent());
	}
	
	private String getDefaultConfigContent() {
		return "hostname = \"EXAMPLE NAME\";  // Server name\r\n" + 
				"password = \"\";              // Password to connect to the server\r\n" + 
				"passwordAdmin = \"\";         // Password to become a server admin\r\n" + 
				" \r\n" + 
				"maxPlayers = 60;            // Maximum amount of players\r\n" + 
				" \r\n" + 
				"verifySignatures = 2;       // Verifies .pbos against .bisign files. (only 2 is supported)\r\n" + 
				"\r\n" + 
				"forceSameBuild = 1;         // When enabled, the server will allow the connection only to clients with same the .exe revision as the server (value 0-1)\r\n" + 
				" \r\n" + 
				"disableVoN = 0;             // Enable/disable voice over network (value 0-1)\r\n" + 
				"vonCodecQuality = 7;        // Voice over network codec quality, the higher the better (values 0-30)\r\n" + 
				" \r\n" + 
				"disable3rdPerson=0;         // Toggles the 3rd person view for players (value 0-1)\r\n" + 
				"disableCrosshair=0;         // Toggles the cross-hair (value 0-1)\r\n" + 
				" \r\n" + 
				"serverTime=\"SystemTime\";    // Initial in-game time of the server. \"SystemTime\" means the local time of the machine. Another possibility is to set the time to some value in \"YYYY/MM/DD/HH/MM\" format, f.e. \"2015/4/8/17/23\" .\r\n" + 
				"serverTimeAcceleration=12;  // Accelerated Time (value 0-24)// This is a time multiplier for in-game time. In this case, the time would move 24 times faster than normal, so an entire day would pass in one hour.\r\n" + 
				"serverTimePersistent=0;     // Persistent Time (value 0-1)// The actual server time is saved to storage, so when active, the next server start will use the saved time value.\r\n" + 
				" \r\n" + 
				"guaranteedUpdates=1;        // Communication protocol used with game server (use only number 1)\r\n" + 
				" \r\n" + 
				"loginQueueConcurrentPlayers=5;  // The number of players concurrently processed during the login process. Should prevent massive performance drop during connection when a lot of people are connecting at the same time.\r\n" + 
				"loginQueueMaxPlayers=500;       // The maximum number of players that can wait in login queue\r\n" + 
				" \r\n" + 
				"instanceId = 1;             // DayZ server instance id, to identify the number of instances per box and their storage folders with persistence files\r\n" + 
				"lootHistory = 1;            // How many persistence history files should be kept by instance, number is looped over during save\r\n" + 
				"storeHouseStateDisabled = false;// Disable houses/doors persistence (value true/false), usable in case of problems with persistence\r\n" + 
				"storageAutoFix = 1;         // Checks if the persistence files are corrupted and replaces corrupted ones with empty ones (value 0-1)\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"class Missions\r\n" + 
				"{\r\n" + 
				"    class DayZ\r\n" + 
				"    {\r\n" + 
				"        template=\"dayzOffline.chernarusplus\"; // Mission to load on server startup. <MissionName>.<TerrainName>\r\n" + 
				"    };\r\n" + 
				"};";
	}

}
