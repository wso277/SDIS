package backup;

import java.io.IOException;

import communication.Communicator;

public class Backup {
	private static BackupReceiveThread brt;
	
	public Backup(String ip, int port) {
		
		brt = new BackupReceiveThread(ip, port);
		
	}

	private static void processMessage(String message) {
		
		
		
	}
	
}
