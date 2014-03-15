package control;

import main.Main;
import restore.RestoreProcessThread;

public class ControlProcessThread extends Thread {

	public static String[] message;
	public static String[] header;

	public ControlProcessThread(String newmessage) {
		header = newmessage.split(" ");
		run();
	}

	@Override
	public void run() {

		if (Main.checkVersion(header[1])) {
			if (header[0] == "GETCHUNK") {
				RestoreProcessThread rpt = new RestoreProcessThread(header[2],
						header[3]);
			} else if (header[0] == "DELETE") {

			} else if (header[0] == "REMOVED") {

			} else if (header[0] == "STORED") {

			} else {
				System.err.println("Operation Invalid!");
			}
		} else {
			System.err.println("Invalid program version!");
		}

	}
}
