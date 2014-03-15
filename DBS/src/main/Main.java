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
	private static Backup backup;
	private static Restore restore;
	private static Control control;
	private static HashMap<String, Address> ipData;

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

	public static Address getipData(String channel) {

		return ipData.get(channel);
	}
}
