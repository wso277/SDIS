package control;

import java.io.IOException;

import main.Main;

import communication.Communicator;

public class Control extends Thread {

	private static String ip;
	private static int port;
	private Communicator ctrlComm;

	public Control(String newip, int newport) {
		ip = newip;
		port = newport;

		try {
			ctrlComm = new Communicator(ip, port);
		} catch (IOException e) {
			System.err.println("Error creating communicator!");
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("Control thread name: "
				+ Thread.currentThread().getName());
		receive();

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
