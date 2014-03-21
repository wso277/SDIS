package backup;

import java.io.IOException;

import communication.Communicator;

public class Backup {

	private static String ip;
	private static int port;
	private static Communicator mdb;
	
	public Backup(String newip, int newport) {
		
		ip = newip;
		port = newport;
		
		System.out.println("Port:" + port);
		System.out.println("Ip:" + ip);
		
		try {
			mdb = new Communicator(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void processMessage(String message) {
		
		
		
	}
	
}
