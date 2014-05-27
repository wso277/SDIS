package space_reclaim;

import java.util.HashMap;

/**
 * Created by João on 27/05/2014.
 */
public class FileChunks {
    private String fileId;
    private HashMap<Integer,Integer> chunksRep;

    public FileChunks(String f) {
        fileId = f;
        chunksRep = new HashMap<>();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String f) {
        fileId = f;
    }

    public HashMap<Integer,Integer> getChunksRep() {
        return chunksRep;
    }

    public void addChunkRep(Integer chunkNo, Integer repDegree) {
        chunksRep.put(chunkNo,repDegree);
    }
}
