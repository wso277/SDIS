package backup;

import java.io.IOException;

import main.Main;
import communication.Communicator;
import control.ControlProcessThread;

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

		receive();
	}

	public void receive() {

		while (true) {

			String mssg = mdbComm.receiveMessage();

		}
	}


}
