package main;

import java.io.Serializable;

public class Chunk implements Serializable, Comparable<Chunk> {

	private static final long serialVersionUID = 1L;
	private String fileId;
	private Integer chunkNo;
	private Integer repDegree;
	private int knowReps;

	public Chunk(String file, Integer chunk, Integer rep) {
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

	public Integer getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(Integer newchunkNo) {
		chunkNo = newchunkNo;
	}

	public Integer getRepDegree() {
		return repDegree;
	}

	public void setRepDegree(Integer newrepDegree) {
		repDegree = newrepDegree;
	}

	public int getKnowReps() {
		return knowReps;
	}

	public void setKnowReps(int newknowReps) {
		knowReps += newknowReps;
	}

	public Integer getCurrentReps() {
		return knowReps - repDegree;
	}

	@Override
	public int compareTo(Chunk o) {
		
		return getCurrentReps();
	}
}
