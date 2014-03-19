package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
	private static ArrayList<Chunk> chunks;
	private static HashMap<StringBuffer, String> fileList;

	public Database() {
		chunks = new ArrayList<Chunk>();
		fileList = new HashMap<StringBuffer, String>();
	}
	
	public void addFile(StringBuffer fileid, String filename) {
	
		fileList.put(fileid, filename);
	}
	
	public String getFile(StringBuffer fileid) {
		
		return fileList.get(fileid);
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
