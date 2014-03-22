package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;

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

	public void removeFile(String fileid) {
		fileList.remove(fileid);

		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).getFileId().equals(fileid)) {
				chunks.remove(i);
				i--;
			}
		}
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

	public void showBackedUpFiles() {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			System.out.println(i + ". " + "[FileId] - " + entry.getKey() + " [FileName] - " + entry.getValue());
		}
	}

	public String getHash(Integer input) {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			if(i == input) {
				return entry.getKey();
			}
		}
		return "fail";		
	}
}
