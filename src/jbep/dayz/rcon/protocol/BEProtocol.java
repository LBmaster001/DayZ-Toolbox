package jbep.dayz.rcon.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * --------------------------------------------------------------------
 * 
 * Java BattlEye RCon Protocol - a battleye protocol library.
    Copyright (C) 2015  Rados³aw Skupnik

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * --------------------------------------------------------------------
 * 
 * Provides methods to work with the BattlEye RCon Protocol.
 * 
 * Official description of the protocol can be found on battleye.com.
 * http://www.battleye.com/downloads/BERConProtocol.txt
 * NOTE: The above link may not be valid.
 * 
 * This class provides the very basic functionality.
 * Please see method javadocs to learn more.
 * 
 * 
 * @author Radoslaw "Myzreal" Skupnik
 *
 */
public class BEProtocol {
	
	/* Some static constants */
	public static final int HEADER_SIZE = 7;
	public static final int SAFE_PACKET_SIZE = 4120;
	
	/* Identifiers of particular packet, as seen in the protocol */
	public static final byte LOGIN_PACKET_ID = 0;
	public static final byte CMD_PACKET_ID = 1;
	public static final byte MSG_PACKET_ID = 2;
	
	/* Local variables */
	private String address;		// The address RCon will connect to.
	private int port;			// The port that should be used.
	private boolean debug;		// Will print raw bytes of sent/received packets.
	private boolean discardMessages = true;		// Default functionality - discards packets of type MSG_PACKET_ID as they are unpredictable and can disturb the order of things.
	private boolean autoMerge = true;	// Default functionality - automatically merges packets of type CMD_PACKET_ID into one.

	private DatagramSocket socket;				// The main UDP socket.
	private Checksum checksum = new CRC32();	// Used to produce the checksum required in all packets.
	private SequenceNumber number = new SequenceNumber();
	
	public BEProtocol(String address, int port) throws SocketException {
		this.address = address;
		this.port = port;
		
		socket = new DatagramSocket();
	}
	
	/**
	 * Attempts to establish a connection.
	 * @throws UnknownHostException - if the host is unknown.
	 */
	public void connect() throws UnknownHostException {
		socket.connect(InetAddress.getByName(address), port);
		
		if (debug)
			System.out.println("Connected");
	}
	
	public void disconnect() {
		if (socket != null) {
			socket.disconnect();
			socket.close();
		}
	}
	
	/**
	 * Attempts to log in by sending a packet of type LOGIN_PACKET_ID.
	 * The server's response is described in the protocol (link in the class description).
	 * Basically if the last byte of the response matters - 01 is login successful.
	 * @param password - the RCon password
	 */
	public void login(String password) {
		/* Get bytes of password */
		byte[] passbytes = password.getBytes();
    	
    	/* Produce the CRC */
    	int crc = crc(bytesToCRC(LOGIN_PACKET_ID, passbytes));
    	
    	/* Create packet */
    	ByteBuffer buffer = packet(crc, LOGIN_PACKET_ID, passbytes);
    	
    	/* Send the data */
    	send(buffer.array(), "Login packet...");
	}
	
	/**
	 * Sends a packet of type CMD_PACKET_ID with the specified command.
	 * Server responds differently to different commands. See the protocol description
	 * (link in the class description) for more details.
	 * @param cmd - command to be executed.
	 */
	public void cmd(String cmd) {
		/* Get bytes of the command */
		byte[] cmdbytes = cmd.getBytes();
		
		/* Gather data to be sent - including a sequence number */
		byte[] temp = new byte[cmdbytes.length+1];
		byte sqn = number.next();
		temp[0] = sqn;
		for (int i = 0; i < cmdbytes.length; i++) {
			temp[i+1] = cmdbytes[i];
		}
		cmdbytes = temp;
		
		/* Produce the CRC */
		int crc = crc(bytesToCRC(CMD_PACKET_ID, cmdbytes));
		
		/* Create packet */
		ByteBuffer buffer = packet(crc, CMD_PACKET_ID, cmdbytes);
		
		/* Send the data */
		send(buffer.array(), "Cmd packet: "+cmd);
	}
	
	/**
	 * Receives a packet and returns the raw bytes.
	 * NOTE: due to the Datagram nature this method blocks until a packet is received.
	 * Due to that you might want to use this method cleverly - wrap it in a thread that listens constantly
	 * and then stores the packets in some array, for example.
	 * @return - raw bytes of the received packet in Little Endian order.
	 * @throws IOException 
	 */
	public byte[] receive() throws IOException {
		/* Create an array of a safe size */
    	byte[] rcv = new byte[SAFE_PACKET_SIZE];
    	
    	/* Receive the Datagram */
    	DatagramPacket packet = new DatagramPacket(rcv, rcv.length);
		socket.receive(packet);
    	
    	/* Drop unnecessary bytes */
    	rcv = new byte[packet.getLength()];
    	System.arraycopy(packet.getData(), 0, rcv, 0, rcv.length);
    	
    	/* Discard messages */
    	if (discardMessages && rcv[7] == MSG_PACKET_ID)
    		return receive();
    	if (rcv.length <= 9)
    		return new byte[0];
    	/* If it's a CMD packet - check for fragmentation - only if autoMerge is on */
    	if (autoMerge && rcv[7] == CMD_PACKET_ID && rcv[9] == 0) {	// 0 at the 9th position means that there is a header indicating the packet is fragmented
    		
    		if (debug)
    			System.out.println("Packet is fragmented!");
    		
    		byte max = rcv[10];		// 10th position in each fragmented packet indicates how many fragments there are
    		byte[][] megapacket = new byte[max][];		// We will gather the fragments in one mega array
    		for (int i = 0; i < max; i++) {
    			
    			if (i > 0) {		// Receive the next packet - since we already have the first one then skip if i == 0
    				rcv = receiveSimple();
    			}
    			
    			byte cur = rcv[11];		// Index of this fragment is stored at the 11th position
    			
    			/* Copy the actual body of the packet to a temporary array */
		    	byte[] temp = new byte[rcv.length-12];
		    	for (int j = 0; j < temp.length; j++) {
		    		temp[j] = rcv[j+12];
		    	}
    			
    			megapacket[cur] = temp;		// Put the byte array in our megapacket
    		}
    		
    		/* Concatenate the fragments into one with a header - note that the crc in the header will not be valid anymore */
    		int size = 9;		// The header + 2 bytes are static.
    		
    		/* Calculate the size */
    		for (byte[] bytes : megapacket) {
    			size += bytes.length;
    		}
    		
    		/* Copy the header 9 bytes first */
    		byte[] temp = new byte[size];
    		for (int i = 0; i < 9; i++) {
    			temp[i] = rcv[i];
    		}
    		
    		/* Concatenate the fragments */
    		int x = 9;
    		for (byte[] bytes : megapacket) {
    			for (byte b : bytes) {
    				temp[x] = b;
    				x++;
    			}
    		}
    		rcv = temp;
    		
    		// Note that at this point x should be equal to size
    		
    		if (debug)
    			System.out.println("Packet was fragmented into "+max+" parts.");
    	}
    	
    	if (debug)
    		debugPrintBytes(rcv, "Received packet...");
    	
    	return rcv;
    }
	
	/**
	 * Sets the timeout for the UDP socket (in milliseconds).
	 * It is mainly used in the receive() function.
	 * @param timeout - timeout in milliseconds.
	 */
	public void setTimeout(int timeout) {
		try {
			if (socket != null)
				socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Whether or not packet of type MSG_PACKET_ID should be discarded.
	 * Default - ON.
	 * Due to the unpredictable nature of these packets they can sometimes disturb the system.
	 * Do not turn this off unless you know what you are doing.
	 * @param dis - true = ON, false = OFF
	 */
	public void setDiscardMessages(boolean dis) {
		discardMessages = dis;
	}
	
	/**
	 * A helper function used to receive fragments of Command packets.
	 * It is not intended for normal use, thus private.
	 * @return - raw bytes of the received packet.
	 */
	private byte[] receiveSimple() {
		/* Create an array of a safe size */
    	byte[] rcv = new byte[SAFE_PACKET_SIZE];
    	
    	/* Receive the Datagram */
    	DatagramPacket packet = new DatagramPacket(rcv, rcv.length);
    	try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
    	/* Drop unnecessary bytes */
    	rcv = new byte[packet.getLength()];
    	System.arraycopy(packet.getData(), 0, rcv, 0, rcv.length);
    	
    	/* Discard messages */
    	if (discardMessages && rcv[7] == MSG_PACKET_ID)
    		rcv = receiveSimple();
    	
    	return rcv;
	}
	
	/**
	 * Sends the data as a DatagramPacket.
	 * Prints the dbgMsg and bytes if debug is ON.
	 * @param data - data to be sent.
	 * @param dbgMsg - debugging message to be printed along with the bytes.
	 */
	private void send(byte[] data, String dbgMsg) {
		if (debug)
			debugPrintBytes(data, dbgMsg);
		
		DatagramPacket packet = new DatagramPacket(data, data.length, socket.getRemoteSocketAddress());
    	try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fills the provided ByteBuffer with header bytes.
	 * Header bytes are always the same (see the protocol)
	 * except for the crc value which you need to provide.
	 * @param buffer - the ByteBuffer to be filled
	 * @param crc - crc value of the subsequent bytes
	 */
	private void header(ByteBuffer buffer, int crc) {
		buffer.put((byte) 0x42);
    	buffer.put((byte) 0x45);
    	buffer.putInt(crc);
    	buffer.put((byte) 0xFF);
	}
	
	/**
	 * Creates a complete packet as a ByteBuffer and returns it.
	 * @param crc - the crc value of the (id+data) part.
	 * @param id - packet identificator.
	 * @param data - the main data of the packet.
	 * @return - a ByteBuffer filled with all the data needed to construct a valid UDP packet.
	 */
	private ByteBuffer packet(int crc, byte id, byte[] data) {
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE+data.length+1);
    	buffer.order(ByteOrder.LITTLE_ENDIAN);
    	header(buffer, crc);
    	buffer.put(id);
    	buffer.put(data);
    	return buffer;
	}
	
	/**
	 * Calculates a CRC32 checksum of the specified data.
	 * @param data - data to be hashed.
	 * @return - a CRC32 checksum (4 bytes).
	 */
	private int crc(byte[] data) {
		checksum.reset();
		checksum.update(data, 0, data.length);
		return (int) checksum.getValue();
	}
	
	/**
	 * A helper function that puts the data that should be hashed in one array.
	 */
	private byte[] bytesToCRC(byte id, byte[] data) {
		byte[] bytesToCRC = new byte[data.length+2];
    	bytesToCRC[0] = (byte) 0xFF;	// Part of header
    	bytesToCRC[1] = id;			// Sequence number
    	for (int i = 0 ; i < data.length; i++) {	// Put the rest
    		bytesToCRC[2+i] = data[i];
    	}
    	return bytesToCRC;
	}
	
	/**
	 * Prints the provided bytes along with the title (if not null).
	 */
	private void debugPrintBytes(byte[] data, String title) {
		if (title != null)
			System.out.println(title);
		for (byte b : data) {
			String byteS = String.format("%02X ", b);
    		System.out.print(byteS+" ");
		}
		System.out.println();
	}
	
	public boolean isConnected() {
		if (socket != null)
			return socket.isConnected();
		else return false;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isAutoMerge() {
		return autoMerge;
	}

	public void setAutoMerge(boolean autoMerge) {
		this.autoMerge = autoMerge;
	}
}
