package de.lbmaster.dayztoolbox.errorreporter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.JsonObject;

import de.lbmaster.dayztoolbox.MainClass;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.Encryption;
import de.lbmaster.dayztoolbox.utils.PathFinder;
import de.lbmaster.dayztoolbox.utils.UIDGenerator;

public class ErrorReporter extends OutputStream implements Runnable {

	private final String host = "toast-teamspeak.de";
	private final int port = 44555;
	private StringBuilder line = new StringBuilder();

	private FileWriter fw = null;

	public ErrorReporter() {
		setupErrorFile();
		System.setErr(new PrintStream(this, true));
		new Thread(this).start();
	}

	@Override
	public void write(int b) throws IOException {
		synchronized (line) {
			line.append((char) b);
		}
	}

	private int lastLineLength;
	private int waitCounter = 0;
	private File errorLog;

	@Override
	public void run() {
		while (true) {
			synchronized (line) {
				int lineLength = line.length();
				if (lineLength > 0) {
					if (lineLength == lastLineLength) {
						waitCounter++;
					}
					lastLineLength = line.length();
					if (waitCounter >= 5) {
						if (sendError()) {
							waitCounter = 0;
							lastLineLength = -1;
							line = new StringBuilder();
						}
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println("ERROR !: " + e.getMessage());
			}
		}
	}

	private void setupErrorFile() {
		try {
			String errorFileLocation = PathFinder.findDayZToolBoxFolder() + "/error.log";
			if (errorFileLocation.startsWith("null")) {
				errorFileLocation = "error.log";
			}
			errorLog = new File(errorFileLocation);
			if (errorLog.exists())
				errorLog.delete();
			errorLog.createNewFile();
			if (!errorLog.exists()) {
				errorLog.getParentFile().mkdirs();
				errorLog.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean sendError() {
		JsonObject rootObj = new JsonObject();
		rootObj.addProperty("javaversion", System.getProperty("java.version"));
		rootObj.addProperty("javavendor", System.getProperty("java.vendor"));
		rootObj.addProperty("javabit", System.getProperty("sun.arch.data.model"));
		rootObj.addProperty("osarch", System.getProperty("os.arch"));
		rootObj.addProperty("osname", System.getProperty("os.name"));
		rootObj.addProperty("osversion", System.getProperty("os.version"));
		rootObj.addProperty("maxmemorybytes", Runtime.getRuntime().maxMemory());
		rootObj.addProperty("freememorybytes", Runtime.getRuntime().freeMemory());
		rootObj.addProperty("totalmemorybytes", Runtime.getRuntime().totalMemory());
		rootObj.addProperty("processorcount", Runtime.getRuntime().availableProcessors());
		rootObj.addProperty("toolboxversion", MainClass.getBuildString());
		String uid = Config.getConfig().getString("uniqueID");
		if (uid == null || uid.length() != 64) {
			uid = UIDGenerator.generate64UID();
			Config.getConfig().setString("uniqueID", uid);
			Config.getConfig().write();
		}
		rootObj.addProperty("uniqueID", uid);
		rootObj.addProperty("errormessage", line.toString());
		String message = rootObj.toString();
		byte[] messageBytes = message.getBytes();
		boolean encrypted = false;
		try {
			byte[] messageBytes2 = Encryption.encryptMessage(messageBytes);
			encrypted = true;
			messageBytes = messageBytes2;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e2) {
			e2.printStackTrace(System.out);
		}

		try {
			fw = new FileWriter(new File(System.getProperty("user.home") + "/DayZTools/error.log"), true);
			fw.write(message);
			fw.flush();
		} catch (IOException e1) {
			System.out.println("ERROR !: could not write to errorlog " + e1.getMessage());
		}
		try {
			Socket s = new Socket(InetAddress.getByName(host), port);
			System.out.println("Connected to Error reporter ? " + s.isConnected());
			if (s.isConnected()) {
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				String messageString = Base64.getEncoder().encodeToString(messageBytes);
				if (messageString.length() >= 65500) {
					out.writeUTF("NEW_" + (encrypted ? "ENC" : "DEC") + "_PACK");
					out.writeInt(messageBytes.length);
					out.write(messageBytes);
					System.out.println("Wrote " + messageBytes.length + " Bytes");
				} else {
					out.writeUTF("NEW_" + (encrypted ? "ENC" : "DEC") + messageString);
				}
				s.close();
				return true;
			}
			s.close();
			return false;
		} catch (IOException e) {
			e.printStackTrace(System.out);
			System.out.println("ERROR !: " + e.getMessage());
		}
		return false;
	}
}
