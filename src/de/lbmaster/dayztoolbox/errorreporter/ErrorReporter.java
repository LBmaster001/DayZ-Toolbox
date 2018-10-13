package de.lbmaster.dayztoolbox.errorreporter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import de.lbmaster.dayztoolbox.MainClass;

public class ErrorReporter extends OutputStream implements Runnable {

	private final String host = "localhost";
	private final int port = 44555;
	private StringBuilder line = new StringBuilder();
	
	private FileWriter fw = null;

	public ErrorReporter() {
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

	private boolean sendError() {
		try {
			fw = new FileWriter(new File(System.getProperty("user.home") + "/DayZTools/error.log"), true);
			fw.write(MainClass.getBuildString() + "\n" + line.toString());
		} catch (IOException e1) {
			System.out.println("ERROR !: could not write to errorlog " + e1.getMessage());
		}
		try {
			Socket s = new Socket(InetAddress.getByName(host), port);
			System.out.println("Connected to Error reporter ? " + s.isConnected());
			if (s.isConnected()) {
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeUTF(MainClass.getBuildString() + "\n" + line.toString());
				s.close();
				return true;
			}
			s.close();
			return false;
		} catch (IOException e) {
			System.out.println("ERROR !: " + e.getMessage());
		}
		return false;
	}
}
