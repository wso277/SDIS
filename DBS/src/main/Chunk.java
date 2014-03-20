package main;

import java.io.Serializable;

public class Chunk implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileId;
	private String chunkNo;
	private String repDegree;
	private int knowReps;

	public Chunk(String file, String chunk, String rep) {
		fileId = file;
		chunkNo = chunk;
		repDegree = rep;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String newfileId) {
		fileId = newfileId;
	}

	public String getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(String newchunkNo) {
		chunkNo = newchunkNo;
	}

	public String getRepDegree() {
		return repDegree;
	}

	public void setRepDegree(String newrepDegree) {
		repDegree = newrepDegree;
	}

	public int getKnowReps() {
		return knowReps;
	}

	public void setKnowReps(int newknowReps) {
		knowReps = newknowReps;
	}

}
