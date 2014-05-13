package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database implements Serializable {

	private static final long serialVersionUID = 1L;
	private final ArrayList<Chunk> chunks;
	private final HashMap<String, String> fileList;

	public Database() {
		chunks = new ArrayList<>();
		fileList = new HashMap<>();
	}

	public synchronized void addFile(String fileid, String filename) {

		fileList.put(fileid, filename);
	}

	public synchronized String getFile(String fileid) {

		return fileList.get(fileid);
	}

	public synchronized void removeFile(String fileid) {
		fileList.remove(fileid);
	}

    public synchronized void removeChunk(String fileid, Integer chunkNo) {
        for (int i = 0; i < chunks.size(); i++) {
            if (chunks.get(i).getFileId().equals(fileid) && chunks.get(i).getChunkNo().equals(chunkNo)) {
                chunks.remove(i);
                break;
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

	synchronized boolean enoughSpace() {
        return getFreeSpace() >= Main.getChunkSize();
    }

	public synchronized float getFreeSpace() {
		return Main.getDiskSize() - chunks.size() * Main.getChunkSize();
	}

	public synchronized void showBackedUpFiles() {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			System.out.println(i + ". " + "[FileId] - " + entry.getKey() + " [FileName] - " + entry.getValue());
			i++;
		}
	}

	public synchronized String getHash(Integer input) {
		int i = 1;
		for (Entry<String, String> entry : fileList.entrySet()) {
			if(i == input) {
				return entry.getKey();
			}
            i++;
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
