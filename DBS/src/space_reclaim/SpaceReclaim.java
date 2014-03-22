package space_reclaim;

import java.util.Collections;

import main.Chunk;
import main.FileManager;
import main.Main;

public class SpaceReclaim extends Thread {
	private Integer spaceToReclaim;
	private Integer chunksToDelete;
	private Chunk[] chunks;
	private boolean delete;

	public SpaceReclaim(Integer newspace) {
		spaceToReclaim = newspace;

		if (Main.getDatabase().getFreeSpace() >= spaceToReclaim) {
			delete = false;
		} else {
			delete = true;
		}

	}

	public void run() {
		if (delete == true) {
			int numberOfChunks = Main.getDatabase().getChunksSize();

			chunksToDelete = (int) Math.ceil((spaceToReclaim - Main
					.getDatabase().getFreeSpace()) / Main.getChunkSize());

			System.out.println(chunksToDelete + "," + numberOfChunks);

			chunks = new Chunk[chunksToDelete];

			Collections.sort(Main.getDatabase().getChunks());

			for (int j = 0; j < chunksToDelete; j++) {
				System.out.println("Ciclo 1");
				chunks[j] = Main.getDatabase().getChunk(
						numberOfChunks - (j - 1));
				System.out.println("Ciclo 1 - pós-codigo-manhoso-vilso");
			}

			for (int j = 0; j < chunksToDelete; j++) {
				System.out.println("Ciclo 2");
				FileManager del = new FileManager(chunks[j].getFileId(), 0,
						true);
				del.deleteChunk(chunks[j].getChunkNo());
				sendMessage(j);
			}
		}

		Main.setDiskSize(Main.getDiskSize() - spaceToReclaim);

		System.out.println("New disk size: " + Main.getDiskSize());

		Main.save();
	}

	private void sendMessage(int j) {
		String mssg = new String("REMOVED " + Main.getVersion() + " "
				+ chunks[j].getFileId() + " " + chunks[j].getChunkNo() + " "
				+ Main.getCRLF() + " " + Main.getCRLF());

		Main.getControl().send(mssg);

	}
}
