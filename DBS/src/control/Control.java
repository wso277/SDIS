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

		System.out.println("Port:" + port);
		System.out.println("Ip:" + ip);
		
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

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Control.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Control.port = port;
	}

	public Communicator getCtrlComm() {
		return ctrlComm;
	}

	public void setCtrlComm(Communicator ctrlComm) {
		this.ctrlComm = ctrlComm;
	}
	
	
}
