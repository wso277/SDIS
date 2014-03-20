package control;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Main;
import communication.Communicator;

public class Control {

	private static String ip;
	private static int port;
	private Communicator ctrlComm;

	public Control(String newip, int newport) {
		ip = newip;
		port = newport;

		try {
			ctrlComm = new Communicator(ip, port);
		} catch (IOException e) {
			System.out.println("Error creating communicator!");
			e.printStackTrace();
		}

	}

	public void receive() {

		while (true) {

			String mssg = ctrlComm.receiveMessage();

			Main.getService().submit(new ControlProcessThread(mssg));

		}
	}

	public void send(String message) {

		ctrlComm.sendMessage(message);
	}

}
