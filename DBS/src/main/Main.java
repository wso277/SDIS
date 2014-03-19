package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cli.Cli;
import restore.Restore;
import communication.Address;
import control.Control;
import backup.Backup;

public class Main {
	public static byte[] CRLF = { 0xD, 0xA };
	public static String version = "1.0";
	public static int diskSize = 50000000;
	public static int chunkSize = 64000;
	private static Backup backup;
	private static Restore restore;
	private static Control control;
	private static Database database = new Database();
	private static HashMap<String, Address> ipData;
	private static ExecutorService service;

	public static void main(String[] args) throws IOException {

		// JAVA QUEUING EXAMPLE
		/* service.submit(new Runnable() { 
		 * 		public void run() {
		 * 			do_some_work();
		 *  	} 
		 * }); // you can submit any number of jobs and the 8
		 * threads will work on them // in order ... // when no more to submit,
		 * call shutdown service.shutdown(); // now wait for the jobs to finish
		 * service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		 */

		// Initializing job queue
		service = Executors.newFixedThreadPool(8);
		
		
		// Store address info
		// ipData.put("mc", new Address(args[0], Integer.parseInt(args[1])));
		// ipData.put("mcb", new Address(args[2], Integer.parseInt(args[3])));
		// ipData.put("mcr", new Address(args[4], Integer.parseInt(args[5])));

		// Temporary IPs for testing
		ipData = new HashMap<String, Address>();
		ipData.put("mc", new Address("224.0.100.1", 7890));
		ipData.put("mcb", new Address("224.0.100.2", 7890));
		ipData.put("mcr", new Address("224.0.100.3", 7890));

		Cli cli = new Cli();
		Cli.run();

		// object backup which creates receive thread
		backup = new Backup(ipData.get("mcb").getIp(), ipData.get("mcb")
				.getPort());

		// object restore which creates restore thread
		restore = new Restore(ipData.get("mcr").getIp(), ipData.get("mcr")
				.getPort());

		// object control which creates control thread
		control = new Control(ipData.get("mc").getIp(), ipData.get("mc")
				.getPort());

		/*
		 * FileManager split = new
		 * FileManager("/home/wso277/Desktop/dropbox.deb", "0");
		 * 
		 * split.split();
		 */

		// FileManager join = new FileManager("[B@3146a9a", "0");

		// join.join();

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

	public static int getDiskSize() {
		return diskSize;
	}

	public static void setDiskSize(int diskSize) {
		Main.diskSize = diskSize;
	}

	public static int getChunkSize() {
		return chunkSize;
	}

	public static void setChunkSize(int chunkSize) {
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
