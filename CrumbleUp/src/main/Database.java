package main;

import space_reclaim.FileChunks;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private static String salt = "#a81nb29";
    private String mail;
    private StringBuffer DbId;
    private StringBuffer passwordHash;
    private byte[] passwordByte;
    private static int diskSize = 100000000;
    private final ArrayList<Chunk> chunks;
    private final HashMap<String, String> fileList;
    //fileReps saves the fileid of the owner backed up files in the network with rep count
    private final HashMap<String, Integer> fileReps;
    private final HashMap<String, Integer> deletedFiles;
    private final ArrayList<FileChunks> filesToBeReclaimed;

    public Database(String newUsername, String newPassword, String newMail) {
        chunks = new ArrayList<>();
        fileList = new HashMap<>();
        deletedFiles = new HashMap<>();
        fileReps = new HashMap<>();
        filesToBeReclaimed = new ArrayList<>();
        username = newUsername;
        mail = newMail;
        passwordHash = getPassHash(newPassword);
        passwordByte = encodePassword(newPassword);
        DbId = getDBHash(username, newPassword, mail);

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
        return passwordHash;
    }

    public boolean login(StringBuffer password) {
        Main.getLogger().log("Pass lida: " + password);
        Main.getLogger().log("Pass DB: " + this.passwordHash);

        if (this.passwordHash == password) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] encodePassword(String password) {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        SecretKeySpec key = null;

        key = new SecretKeySpec(salt.getBytes(StandardCharsets.ISO_8859_1), "DES");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key/*, new IvParameterSpec(Main.getDatabase().getUsernameByte())*/);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }/* catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }*/

        try {
            return cipher.doFinal(password.getBytes(StandardCharsets.ISO_8859_1));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getMail() {
        return mail;
    }

    public static StringBuffer getDBHash(String username, String password, String mail) {
        String hash = username + password + mail;

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hashFileName = null;

        try {
            assert digest != null;
            hashFileName = digest.digest(hash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        /*
        Method to convert a byte array to hexadecimal form found on StackOverflow
        */
        StringBuffer hashString = new StringBuffer();
        for (byte aHashFileName : hashFileName) {
            hashString.append(Integer.toString((aHashFileName & 0xff) + 0x100, 16).substring(1));
        }
        return hashString;
    }

    public static StringBuffer getPassHash(String password) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hashFileName = null;

        try {
            assert digest != null;
            hashFileName = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        /*
        Method to convert a byte array to hexadecimal form found on StackOverflow
        */
        StringBuffer hashString = new StringBuffer();
        for (byte aHashFileName : hashFileName) {
            hashString.append(Integer.toString((aHashFileName & 0xff) + 0x100, 16).substring(1));
        }
        return hashString;
    }

    public StringBuffer getDbId() {
        return DbId;
    }

    public void checkForLostChunks() {
        for (Chunk chunk : chunks) {
            if (!FileManager.fileExists(username + "/" + chunk.getFileId() + "/" + chunk.getChunkNo() + ".part")) {
                for (FileChunks file : Main.getDatabase().getFilesToBeReclaimed()) {
                    if (file.getFileId().equals(chunk.getFileId())) {
                        Main.getDatabase().getFilesToBeReclaimed().remove(file);
                        break;
                    }
                }

                FileManager del = new FileManager(chunk.getFileId(), 0, true);
                del.deleteFile();

                String msg = "DELETED " + chunk.getFileId() + Main.getCRLF() + Main.getCRLF();
                Main.getLogger().log("Sent: " + msg);
                Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));

            }
        }
    }

    public byte[] getPasswordByte() {
        return passwordByte;
    }
}
