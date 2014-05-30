package main;

import space_reclaim.FileChunks;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private byte[] passwordByte;
    private StringBuffer passwordStr;
    private static int diskSize = 100000000;
    private final ArrayList<Chunk> chunks;
    private final HashMap<String, String> fileList;
    //fileReps saves the fileid of the owner backed up files in the network with rep count
    private final HashMap<String, Integer> fileReps;
    private final HashMap<String, Integer> deletedFiles;
    private final ArrayList<FileChunks> filesToBeReclaimed;

    public Database(String username, String password) {
        chunks = new ArrayList<>();
        fileList = new HashMap<>();
        deletedFiles = new HashMap<>();
        fileReps = new HashMap<>();
        filesToBeReclaimed = new ArrayList<>();
        this.username = username;
        this.password = password;
        encodePassword(password);
    }

    public static int getDiskSize() {
        return diskSize;
    }

    public static void setDiskSize(int diskSize) {
        Database.diskSize = diskSize;
    }

    public synchronized void addFileRep(String fileId, Integer rep) {
        if (fileReps.get(fileId) == null || rep > fileReps.get(fileId)) {
            fileReps.put(fileId, rep);
        }
    }

    public synchronized Integer getFileRep(String fileId) {
        return fileReps.get(fileId);
    }

    public ArrayList<FileChunks> getFilesToBeReclaimed() {
        return filesToBeReclaimed;
    }

    public void addFileToBeReclaimed(FileChunks f) {
        filesToBeReclaimed.add(f);
    }

    public synchronized HashMap<String, Integer> getDeletedFiles() {
        return deletedFiles;
    }

    public synchronized void changeRepDegree(String fileId, Integer repDegree) {
        if (repDegree.equals(0)) {
            deletedFiles.remove(fileId);
        } else {
            deletedFiles.put(fileId, repDegree);
        }

    }

    public synchronized void addFile(String fileId, String filename) {

        fileList.put(fileId, filename);
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
        return getDiskSize() - chunks.size() * Main.getChunkSize();
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
            if (i == input) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public StringBuffer getPassword() {
        return passwordStr;
    }

    public boolean login(String password) {
        if (this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    private void encodePassword(String password) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            assert digest != null;
            passwordByte = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    /*
    Method to convert a byte array to hexadecimal form found on StackOverflow
     */
        passwordStr = new StringBuffer();
        for (byte aHashFileName : passwordByte) {
            passwordStr.append(Integer.toString((aHashFileName & 0xff) + 0x100, 16).substring(1));
        }

    }
}
