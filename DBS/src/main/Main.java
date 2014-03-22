package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cli.Cli;
import restore.Restore;
import communication.Address;
import control.Control;
import backup.Backup;

public class Main implements Serializable {

	private static final long serialVersionUID = 1L;
	public static byte[] CRLF = { 0xD, 0xA };
	public static String version = "1.0";
	public static int diskSize = 1200000;
	public static int chunkSize = 64000;
	private static Backup backup;
	private static Restore restore;
	private static Control control;
	private static Cli cli;
	private static Database database;
	private static HashMap<String, Address> ipData;
	private static ExecutorService service;

	public static void main(String[] args) throws IOException {

		// JAVA QUEUING EXAMPLE
		/*
		 * service.shutdown(); // now wait for the jobs to finish
		 * service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		 */

		// Load database
		load();

		FileManager split = new FileManager("/home/wso277/Desktop/image1.jpg", 0, false);
		split.split();
		FileManager split1 = new FileManager("/home/wso277/Desktop/image2.jpg", 0, false);
		split1.split();
		FileManager split2 = new FileManager("/home/wso277/Desktop/image3.jpg", 0, false);
		split2.split();
		
		// Initializing job queue
		service = Executors.newFixedThreadPool(12);

		// Store address info
		ipData = new HashMap<String, Address>();

		// Temporary IPs for testing
		/*ipData.put("mc", new Address("mc", "226.0.100.1", 7891));
		ipData.put("mcb", new Address("mcb", "226.0.100.2", 7892));
		ipData.put("mcr", new Address("mcr", "226.0.100.3", 7893));*/

		// Initializing components
		cli = new Cli();
		backup = new Backup(ipData.get("mcb").getIp(), ipData.get("mcb")
				.getPort());
		control = new Control(ipData.get("mc").getIp(), ipData.get("mc")
				.getPort());

		// Pushing main components to job queue
		service.submit(backup);
		service.submit(control);
		service.submit(cli);

		// save database
		save();
	}
	
	public synchronized static void save() {

		ObjectOutputStream save = null;

		try {
			save = new ObjectOutputStream(new FileOutputStream("database.dbs"));
		} catch (FileNotFoundException e) {
			System.err.println("Database.dbs not found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error creating database.dbs");
			e.printStackTrace();
		}

		try {
			save.writeObject(database);
		} catch (IOException e) {
			System.err.println("Error saving database");
			e.printStackTrace();
		}
	}

	public static void load() {

		ObjectInputStream load = null;
		Boolean newdb = false;

		try {
			load = new ObjectInputStream(new FileInputStream("database.dbs"));
		} catch (FileNotFoundException e) {
			database = new Database();
			newdb = true;
		} catch (IOException e) {
			System.err.println("Error creating database.dbs");
			e.printStackTrace();
		}

		if (!newdb) {

			try {
				database = (Database) load.readObject();
				System.out.println("Read database!");
			} catch (ClassNotFoundException e) {
				System.err.println("Database not found!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error loading database!");
				e.printStackTrace();
			}
		}
	}

	public static void saveNetwork(Address mc, Address mcr, Address mcb) {

		ObjectOutputStream save = null;

		try {
			save = new ObjectOutputStream(new FileOutputStream("network.dbs"));
		} catch (FileNotFoundException e) {
			System.err.println("Network.dbs not found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error creating network.dbs");
			e.printStackTrace();
		}

		try {
			save.writeObject(mc);
			save.writeObject(mcr);
			save.writeObject(mcb);
		} catch (IOException e) {
			System.err.println("Error saving network configurations");
			e.printStackTrace();
		}
	}

	public static void loadNetwork() {

		ObjectInputStream load = null;
		Boolean newdb = false;

		try {
			load = new ObjectInputStream(new FileInputStream("network.dbs"));
		} catch (FileNotFoundException e) {
			System.err.println("Network.dbs not found!");
		} catch (IOException e) {
			System.err.println("Error creating network.dbs");
			e.printStackTrace();
		}

		if (!newdb) {

			try {
				ipData.put("mc", (Address) load.readObject());
				ipData.put("mcr", (Address) load.readObject());
				ipData.put("mcb", (Address) load.readObject());
				System.out.println("\nRead network configurations!");
			} catch (ClassNotFoundException e) {
				System.err.println("network.dbs not found!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error loading network configurations!");
				e.printStackTrace();
			}
		}
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

	public static ExecutorService getService() {
		return service;
	}

	public static Backup getBackup() {
		return backup;
	}

	public static void setBackup(Backup backup) {
		Main.backup = backup;
	}

	public static Restore getRestore() {
		return restore;
	}

	public static void setRestore(Restore restore) {
		Main.restore = restore;
	}

	public static Control getControl() {
		return control;
	}

	public static void setControl(Control control) {
		Main.control = control;
	}

	public static Cli getCli() {
		return cli;
	}

	public static void setCli(Cli cli) {
		Main.cli = cli;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static void setService(ExecutorService service) {
		Main.service = service;
	}

	
}
