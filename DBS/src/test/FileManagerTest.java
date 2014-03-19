package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Database;
import main.FileManager;
import cli.Cli;
import restore.Restore;
import communication.Address;
import control.Control;
import backup.Backup;

public class FileManagerTest {
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

		/*
		  FileManager split = new
		  FileManager("/home/wso277/Desktop/image.jpg", "0");
		  
		  split.split();
		 */

		
		FileManager join = new FileManager(
				"e59a02919cea39d320130a517e8c27847fb4ae3e3320fe710e9b0755d3a737de",
				"0");

		join.join();

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
		FileManagerTest.version = version;
	}

	public static int getDiskSize() {
		return diskSize;
	}

	public static void setDiskSize(int diskSize) {
		FileManagerTest.diskSize = diskSize;
	}

	public static int getChunkSize() {
		return chunkSize;
	}

	public static void setChunkSize(int chunkSize) {
		FileManagerTest.chunkSize = chunkSize;
	}

	public static HashMap<String, Address> getIpData() {
		return ipData;
	}

	public static void setIpData(HashMap<String, Address> ipData) {
		FileManagerTest.ipData = ipData;
	}

	public static Database getDatabase() {
		return database;
	}

	public static void setDatabase(Database database) {
		FileManagerTest.database = database;
	}

}
