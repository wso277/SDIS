package main;

import java.io.Serializable;

public class Chunk implements Serializable, Comparable<Chunk> {

    private static final long serialVersionUID = 1L;
    private String fileId;
    private Integer chunkNo;
    private Integer repDegree;
    private Integer knownReps;
    private Boolean sent;

    public Chunk(String file, Integer chunk, Integer rep) {
        fileId = file;
        chunkNo = chunk;
        repDegree = rep;
        knownReps = 0;
        sent = false;
    }

    public String getFileId() {
        return fileId;
    }

    public synchronized void setFileId(String newfileId) {
        fileId = newfileId;
    }

    public Integer getChunkNo() {
        return chunkNo;
    }

    public synchronized void setChunkNo(Integer newchunkNo) {
        chunkNo = newchunkNo;
    }

    public Integer getRepDegree() {
        return repDegree;
    }

    public synchronized void setRepDegree(Integer newrepDegree) {
        repDegree = newrepDegree;
    }

    public int getKnownReps() {
        return knownReps;
    }

    public synchronized void setKnownReps(int newknowReps) {
        knownReps += newknowReps;
    }

    Integer getCurrentReps() {
        return knownReps - repDegree;
    }

    public Boolean getSent() {
        return sent;
    }

    public synchronized void setSent(Boolean newsent) {
        sent = newsent;
    }

    @Override
    public int compareTo(Chunk o) {

        return getCurrentReps();
    }
}
