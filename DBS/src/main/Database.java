package main;

import java.util.ArrayList;

public class Database {
	private static ArrayList<Chunk> chunks;

	public Database() {
		chunks = new ArrayList<Chunk>();
	}

	public boolean addChunk(Chunk chunk) {
		if (enoughSpace()) {
			chunks.add(chunk);
			return true;
		} else {
			return false;
		}
	}

	public boolean enoughSpace() {
		if (getFreeSpace() >= Main.getChunkSize()) {
			return true;
		}
		return false;
	}

	public static float getFreeSpace() {
		return Main.getDiskSize() - chunks.size() * Main.getChunkSize();
	}
}
