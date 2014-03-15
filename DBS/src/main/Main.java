package main;

import java.io.IOException;
import java.util.HashMap;

import restore.Restore;
import communication.Address;
import control.Control;
import backup.Backup;

public class Main {
	public static byte[] CRLF = { 0xD, 0xA };
	public static String version = "1.0";
	public static float diskSize = 50000000;
	public static float chunkSize = 64000;
	private static Backup backup;
	private static Restore restore;
	private static Control control;
	private static HashMap<String, Address> ipData;
	private static Database database;

	public static void main(String[] args) throws IOException {

		// Store address info
		ipData.put("mc", new Address(args[0], Integer.parseInt(args[1])));
		ipData.put("mcb", new Address(args[2], Integer.parseInt(args[3])));
		ipData.put("mcr", new Address(args[4], Integer.parseInt(args[5])));

		// object backup which creates receive thread
		backup = new Backup(ipData.get("mcb").getIp(), ipData.get("mcb")
				.getPort());

		// object restore which creates restore thread
		restore = new Restore(ipData.get("mcr").getIp(), ipData.get("mcr")
				.getPort());

		// object control which creates control thread
		control = new Control(ipData.get("mc").getIp(), ipData.get("mc")
				.getPort());
	}

	public static boolean checkVersion(String ver) {
		if (version.equals(ver)) {
			return true;
		} else {
			return false;
		}
	}

	public static Address getipData(String channel) {
		return ipData.get(channel);
	}

	public static byte[] getCRLF() {
		return CRLF;
	}

	public static void setCRLF(byte[] cRLF) {
		CRLF = cRLF;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		Main.version = version;
	}

	public static float getDiskSize() {
		return diskSize;
	}

	public static void setDiskSize(float diskSize) {
		Main.diskSize = diskSize;
	}

	public static float getChunkSize() {
		return chunkSize;
	}

	public static void setChunkSize(float chunkSize) {
		Main.chunkSize = chunkSize;
	}

	public static HashMap<String, Address> getIpData() {
		return ipData;
	}

	public static void setIpData(HashMap<String, Address> ipData) {
		Main.ipData = ipData;
	}

	public static Database getDatabase() {
		return database;
	}

	public static void setDatabase(Database database) {
		Main.database = database;
	}

}
