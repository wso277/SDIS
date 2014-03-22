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


}
