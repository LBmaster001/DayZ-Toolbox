package de.lbmaster.dayztoolbox.guis.servermanager;

public class ServerEvent {
	
	private ServerEventType type = ServerEventType.EVENT_NONE;
	
	public ServerEvent(ServerEventType type) {
		this.type = type;
	}
	
	public ServerEventType getType() {
		return type;
	}
}
