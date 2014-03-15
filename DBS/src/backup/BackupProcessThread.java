package backup;

import main.Main;
import communication.Communicator;

public class BackupProcessThread extends Thread{

	public static String[] message;
	public static String[] header;
	public static String body;
	
	public BackupProcessThread(String newmessage) {
		this.message = newmessage.split(Main.CRLF.toString() + Main.CRLF.toString());
		header = message[0].split(" ");
		body = message[1];
		run();
	}

	@Override
	public void run() {
		if(header[0]=="PUTCHUNK"){
			
		} else {
			System.out.println("Invalid Message");
		}
		
		
	}
}
