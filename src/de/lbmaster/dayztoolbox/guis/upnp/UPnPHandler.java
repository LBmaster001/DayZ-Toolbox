package de.lbmaster.dayztoolbox.guis.upnp;

import java.io.IOException;
import java.net.InetAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.GatewayDiscover;
import org.xml.sax.SAXException;

public class UPnPHandler {
	
	private GatewayDevice device;
	
	public UPnPHandler() {
		
	}
	
	public boolean connectToGateway() {
		GatewayDiscover discover = new GatewayDiscover();
		try {
			discover.discover();
			device = discover.getValidGateway();
			if (device != null)
				return true;
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addPorts() throws IOException, SAXException {
		if (device == null)
			return false;
		InetAddress local = device.getLocalAddress();
		boolean ok = true;
		if (!device.addPortMapping(2302, 2302, local.getHostAddress(), "UDP", "DayZ Standalone Server"))
			ok = false;
		if (!device.addPortMapping(2303, 2303, local.getHostAddress(), "UDP", "DayZ Standalone Server"))
			ok = false;
		if (!device.addPortMapping(2304, 2304, local.getHostAddress(), "UDP", "DayZ Standalone Server"))
			ok = false;
		if (!device.addPortMapping(27016, 27016, local.getHostAddress(), "UDP", "DayZ Standalone Server"))
			ok = false;
		if (!ok)
			removePorts();
		return ok;
	}
	
	public boolean removePorts() throws IOException, SAXException {
		if (device == null)
			return false;
		boolean ok = true;
		if (!device.deletePortMapping(2302, "UDP"))
			ok = false;
		if (!device.deletePortMapping(2303, "UDP"))
			ok = false;
		if (!device.deletePortMapping(2304, "UDP"))
			ok = false;
		if (!device.deletePortMapping(27016, "UDP"))
			ok = false;
		return ok;
	}

}
