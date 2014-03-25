package backup;

import main.FileManager;
import main.Main;

public class BackupSendThread extends Thread {
	private static String message;
	private static String fileHash;
	private static FileManager fm;
	boolean enoughSpace;

	public BackupSendThread(String filePath, Integer repDegree) {
		fm = new FileManager(filePath, repDegree, false);
		fileHash = fm.getHashString().toString();

		if (fm.split()) {
			enoughSpace = true;
		} else {
			enoughSpace = false;
		}
	}

	public void run() {
		if (enoughSpace) {
			Integer chunkNo = 1;
			while (fm.readChunk(chunkNo)) {

				message = "PUTCHUNK " + Main.getVersion() + " " + fileHash
						+ " " + chunkNo + " " + fm.getRep() + Main.getCRLF()
						+ " " + Main.getCRLF() + fm.getChunkData();

				chunkNo++;
				
				/*Main.getBackup().getMdbComm().sendMessage(message);*/
				
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
