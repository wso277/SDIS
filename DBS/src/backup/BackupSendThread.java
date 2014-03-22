package backup;

import main.FileManager;
import main.Main;

public class BackupSendThread extends Thread {
	String message;
	String fileHash;
	boolean enoughSpace;

	public BackupSendThread(String filePath, Integer repDegree) {
		FileManager fm = new FileManager(filePath, repDegree, false);
		fileHash = fm.getHashString().toString();
		
		if (fm.split()) {
			enoughSpace = true;
		} else {
			enoughSpace = false;
		}
	}

	public void run() {
		if (enoughSpace) {
			for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
				if ( Main.getDatabase().getChunks().get(i).getFileId() == fileHash) {
					
				}
			}
		}
	}
}
