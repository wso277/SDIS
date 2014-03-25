package backup;

import main.Main;

public class BackupProcessThread extends Thread {

	public String[] message;
	public String[] header;
	public String body;

	public BackupProcessThread(String newmessage) {
		this.message = newmessage.split(Main.getCRLF().toString()
				+ Main.getCRLF().toString());
		header = message[0].split(" ");
		body = message[1];
		run();
	}

	@Override
	public void run() {
		if (header[0] == "PUTCHUNK" && Main.checkVersion(header[1])
				&& Main.getDatabase().enoughSpace()) {
			
		} else {
			System.out.println("Invalid Message");
		}
	}
}
