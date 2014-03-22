package spaceReclaim;

import java.util.Collections;

import main.Chunk;
import main.FileManager;
import main.Main;

public class SpaceReclaim extends Thread {
	private Integer spaceToReclaim;
	private Integer chunksToDelete;
	private Chunk[] chunks;

	public SpaceReclaim(Integer newspace) {
		spaceToReclaim = newspace;

		chunksToDelete = spaceToReclaim / Main.getDiskSize() + 1;

		chunks = new Chunk[chunksToDelete];
	}

	public void run() {

		Collections.sort(Main.getDatabase().getChunks());

		for (int j = 0; j < chunksToDelete; j++) {

			chunks[j] = Main.getDatabase().getChunk(
					Main.getDatabase().getChunksSize() - j);
		}

		for (int j = 0; j < chunksToDelete; j++) {
			FileManager del = new FileManager(chunks[j].getFileId(), 0, true);
			del.deleteChunk(chunks[j].getChunkNo());
			sendMessage(j);
		}
	}

	private void sendMessage(int j) {
		String mssg = new String("REMOVED " + Main.getVersion() + " "
				+ chunks[j].getFileId() + " " + chunks[j].getChunkNo() + " "
				+ Main.getCRLF() + " " + Main.getCRLF());
		
		Main.getControl().send(mssg);

	}
}
