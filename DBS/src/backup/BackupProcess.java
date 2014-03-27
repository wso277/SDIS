package backup;

import main.Main;

import java.util.ArrayList;

public class BackupProcess extends Thread {

	public String message;
	public ArrayList<String> header;

	public BackupProcess(String newmessage) {
        message = newmessage;
	}

	@Override
	public void run() {

        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
        }

		if (header.get(0).equals("PUTCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {

            }
        } else {
			System.out.println("Invalid Message");
		}
	}
}
