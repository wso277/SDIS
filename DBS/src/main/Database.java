package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Chunk> chunks;
	private HashMap<String, String> fileList;

	public Database() {
		chunks = new ArrayList<Chunk>();
		fileList = new HashMap<String, String>();
	}

	public synchronized void addFile(String fileid, String filename) {

		fileList.put(fileid, filename);
	}

	public synchronized String getFile(String fileid) {

		return fileList.get(fileid);
	}

	public synchronized void removeFile(String fileid) {
		fileList.remove(fileid);

		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).getFileId().equals(fileid)) {
				chunks.remove(i);
				i--;
			}
		}
	}

	public synchronized boolean addChunk(Chunk chunk) {
		if (enoughSpace()) {
			chunks.add(chunk);
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean enoughSpace() {
		if (getFreeSpace() >= Main.getChunkSize()) {
			return true;
		}
		return false;
	}

	public synchronized float getFreeSpace() {
		return Main.getDiskSize() - chunks.size() * Main.getChunkSize();
	}

	public synchronized void showBackedUpFiles() {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			System.out.println(i + ". " + "[FileId] - " + entry.getKey() + " [FileName] - " + entry.getValue());
		}
	}

	public synchronized String getHash(Integer input) {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			if(i == input) {
				return entry.getKey();
			}
		}
		return "fail";		
	}
	
	public synchronized Chunk getChunk(Integer num) {
		return chunks.get(num);
	}
	
	public synchronized Integer getChunksSize() {
		return chunks.size();
	}
	
	public synchronized ArrayList<Chunk> getChunks() {
		return chunks;
	}
}
