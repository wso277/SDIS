package main;

import java.io.Serializable;

public class Chunk implements Serializable{

	private static final long serialVersionUID = 1L;
	private static String fileId;
	private static String chunkNo;
	private static String repDegree;
	private static int knowReps;
	
	public Chunk(String file, String chunk, String rep) {
		fileId = file;
		chunkNo = chunk;
		repDegree = rep;		
	}
	
	public static String getFileId() {
		return fileId;
	}
	public static void setFileId(String fileId) {
		Chunk.fileId = fileId;
	}
	public static String getChunkNo() {
		return chunkNo;
	}
	public static void setChunkNo(String chunkNo) {
		Chunk.chunkNo = chunkNo;
	}
	public static String getRepDegree() {
		return repDegree;
	}
	public static void setRepDegree(String repDegree) {
		Chunk.repDegree = repDegree;
	}
	public static int getKnowReps() {
		return knowReps;
	}
	public static void setKnowReps(int knowReps) {
		Chunk.knowReps = knowReps;
	}
	
	
}
