package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable {
	
	/**
	 * Serialize id
	 */
	private final long serialVersionUID = 1L;
	private ArrayList<Chunk> chunks;
	private HashMap<String, String> fileList;

	public Database() {
		chunks = new ArrayList<Chunk>();
		fileList = new HashMap<String, String>();
	}
	
	public void addFile(String fileid, String filename) {
	
		fileList.put(fileid, filename);
	}
	
	public String getFile(String fileid) {
		
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

	public float getFreeSpace() {
		return Main.getDiskSize() - chunks.size() * Main.getChunkSize();
	}
}
