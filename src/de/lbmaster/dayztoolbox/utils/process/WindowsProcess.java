package de.lbmaster.dayztoolbox.utils.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WindowsProcess {

	private String exe;
	private String title;
	private String user;
	private int pid;
	private long memusage;
	private ProcessStatus status;
	private long lifetime;

	public static List<WindowsProcess> findDayZServerProcesses() {
		List<WindowsProcess> processes = new ArrayList<WindowsProcess>();
		try {
			String line;
			String[] cmd = new String[] { System.getenv("windir") + "\\system32\\" + "tasklist.exe", "/NH", "/FO", "CSV" };
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().contains("dayzserver")) {
					int pidStart = line.indexOf(",\"") + 2;
					int pid = Integer.parseInt(line.substring(pidStart, line.indexOf("\"", pidStart)));
					WindowsProcess process = new WindowsProcess();
					process.pid = pid;
					process.getFurtherInformation();
					processes.add(process);
				}
			}
			System.out.println(p.isAlive());
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}

		return processes;
	}

	public static WindowsProcess findDayZServerProcess(int port) {
		List<WindowsProcess> list = findDayZServerProcesses();
		System.out.println("Found DayZ Server Processes: " + list.size());
		if (list == null || list.isEmpty())
			return null;
		for (WindowsProcess p : list) {
			if (p.title != null && p.title.contains("" + port)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return pid + " " + exe + " " + title + " " + status.toString() + " " + memusage + " " + user + " " + lifetime;
	}

	public void endProcess() {
		String[] cmd = new String[] { System.getenv("windir") + "\\system32\\" + "taskkill.exe", "/PID", pid + "" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getFurtherInformation() {
		try {
			String line;
			String[] cmd = new String[] { System.getenv("windir") + "\\system32\\" + "tasklist.exe", "/V", "/NH", "/FO", "CSV", "/FI", "\"PID eq " + pid + "\"" };
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().contains("dayzserver")) {
					String[] allStrings = findAllStrings(line);
					if (allStrings.length >= 9) {
						this.exe = allStrings[0];
						this.memusage = Long.parseLong(allStrings[4].replaceAll("[^0-9]", ""));
						this.status = ProcessStatus.valueOf(allStrings[5].toUpperCase().replace(" ", "_"));
						this.user = allStrings[6];
						this.lifetime = 3600000 + (new SimpleDateFormat("HH:mm:ss").parse(allStrings[7].length() <= 7 ? "0" + allStrings[7] : allStrings[7]).getTime());
						this.title = allStrings[8];
					} else {
						this.status = ProcessStatus.UNKNOWN;
					}
				}
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private String[] findAllStrings(String s) {
		int[] quoteIndices = getQuoteIndices(s);
		String[] strings = new String[quoteIndices.length / 2];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = s.substring(quoteIndices[i * 2] + 1, quoteIndices[i * 2 + 1]);
		}
		return strings;
	}

	private int[] getQuoteIndices(String s) {
		char[] chars = s.toCharArray();
		int[] indices = new int[s.length() - s.replace("\"", "").length()];
		int index = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '"') {
				indices[index++] = i;
			}
		}
		return indices;
	}

}
