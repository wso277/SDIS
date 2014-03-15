package backup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

import communication.Communicator;

public class BackupReceiveThread extends Thread {

	public static Communicator mcb;
	public static String ip;
	public static String response;
	public static DatagramPacket packet;
	public static int PSIZE = 64000;
	public static int port;
	public static byte[] rbuf;

	public BackupReceiveThread(String newip, int newport) {
		ip = newip;
		port = newport;
		
		try {
			mcb = new Communicator(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		run();
	}

	@Override
	public void run() {
		while (true) {

			response = mcb.receiveMessage();
			
			BackupProcessThread bpt =  new BackupProcessThread();

		}
	}
}
