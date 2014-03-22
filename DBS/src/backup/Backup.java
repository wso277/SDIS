package backup;

import java.io.IOException;

import communication.Communicator;

public class Backup extends Thread {

	private static String ip;
	private static int port;
	private static Communicator mdbComm;

	public Backup(String newip, int newport) {

		ip = newip;
		port = newport;

		try {
			mdbComm = new Communicator(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("Backup thread name: "
				+ Thread.currentThread().getName());
		receive();
	}
	
	public void receive() {

		while (true) {

			String mssg = mdbComm.receiveMessage();

		}
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Backup.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Backup.port = port;
	}

	public static Communicator getMdbComm() {
		return mdbComm;
	}

	public static void setMdbComm(Communicator mdbComm) {
		Backup.mdbComm = mdbComm;
	}


}
