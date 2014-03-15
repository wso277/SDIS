package control;

import java.io.IOException;
import java.net.DatagramPacket;

import backup.BackupProcessThread;

import communication.Communicator;

public class ControlReceiveThread extends Thread {

	public static Communicator mc;
	public static String ip;
	public static String message;
	public static int port;

	public ControlReceiveThread(String newip, int newport) {
		ip = newip;
		port = newport;

		try {
			mc = new Communicator(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		run();
	}

	@Override
	public void run() {
		while (true) { // mudar para variavel para fechar o servidor?

			message = mc.receiveMessage();

			ControlProcessThread cpt = new ControlProcessThread(message);

		}
	}
}
