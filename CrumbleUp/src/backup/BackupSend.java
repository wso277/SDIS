package backup;

import main.Chunk;
import main.FileManager;
import main.Main;
import space_reclaim.FileChunks;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BackupSend extends Thread {
    private final String fileHash;
    private final FileManager fm;
    private Integer time;
    private Integer storeds;
    private Integer tries;
    private Integer chunkN;
    private int knownReps;
    private int repDegree;
    private Boolean isFile;
    private Boolean sent = false;

    public BackupSend(String filePath, Integer repDegree, Boolean file, Integer newChunkNo, int knownReps) {
        if (file) {
            fm = new FileManager(filePath, repDegree, false);
        } else {
            fm = new FileManager(filePath, repDegree, true);
        }
        fileHash = fm.getHashString().toString();

        time = 500;
        storeds = 0;
        tries = 0;
        isFile = file;
        chunkN = newChunkNo;
        this.knownReps = knownReps;
        this.repDegree = repDegree;
    }

    public void run() {

        if (isFile) {
            Boolean enoughSpace;
            enoughSpace = fm.split();

            if (enoughSpace) {
                Integer chunkNo = 0;

                while (fm.readChunk(chunkNo)) {

                    String msg;
                    msg = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                            Main.getCRLF() + Main.getCRLF();

                    Main.getLogger().log("Sent: " + msg);

                    byte[] mssg = msg.getBytes(StandardCharsets.ISO_8859_1);
                    byte[] mssg1 = Main.appendArray(mssg, fm.getChunkData());

                    if(repDegree != -1) {
                        while (storeds < fm.getRep() && tries < 5) {

                            Main.getBackup().send(mssg1);

                            try {
                                sleep(time);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            tries++;
                            time += time;
                        }
                    } else {
                        Main.getBackup().send(mssg1);
                    }

                    int reps = 0;
                    for (int i = 0; i < Main.getDatabase().getChunks().size(); i++) {
                        Chunk c = Main.getDatabase().getChunk(i);
                        if (c.getFileId().equals(fileHash) && c.getChunkNo().equals(chunkNo)) {
                            reps = c.getKnownReps();
                        }
                    }

                    Main.getDatabase().addFileRep(fileHash, reps);

                    if (reps < fm.getRep()) {
                        System.err.println("Chunk number " + chunkNo + " replicated only " + reps + " " +
                                "times!");
                    }

                    fm.deleteChunk(chunkNo);

                    chunkNo++;
                    storeds = 0;
                    time = 500;
                    tries = 0;
                    sendPutDB();
                }
            } else {
                System.out.println("Not enough space for backup!");
            }

            System.out.println("\nFinished backup of fileid " + fileHash);
        } else {
            backupChunk(chunkN);
        }
    }

    private void sendPutDB() {
        StringBuffer hash = getDBHash();
        Main.getService().submit(new BackupSend(hash.toString(),-1,false,0,0));
    }

    private StringBuffer getDBHash() {
        String hash = Main.getDatabase().getUsername() + Main.getDatabase().getPassword();

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

    public void backupChunk(Integer chunkNo) {

        fm.readChunk(chunkNo);

        String message;
        message = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                Main.getCRLF() + Main.getCRLF();

        Main.getLogger().log("Sent: " + message);

        byte[] mssg = message.getBytes(StandardCharsets.ISO_8859_1);
        byte[] mssg1 = Main.appendArray(mssg, fm.getChunkData());

        Random r = new Random();
        int time = r.nextInt(401);

        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!sent) {

            while (storeds < fm.getRep() && tries < 5) {

                Main.getBackup().send(mssg1);

                try {
                    sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                tries++;
                time += time;
            }

            boolean found = false;
            for (FileChunks file : Main.getDatabase().getFilesToBeReclaimed()) {
                if (file.getFileId().equals(fileHash)) {
                    file.addChunkRep(chunkNo, knownReps + storeds);
                    if (knownReps + storeds >= fm.getRep()) {
                        file.getChunksRep().remove(chunkNo);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                FileChunks file = new FileChunks(fileHash, repDegree);
                Main.getDatabase().addFileToBeReclaimed(file);
            }
        }

        if (storeds + knownReps < fm.getRep()) {
            System.out.println("Backup timed out for chunk no " + chunkNo + ". Achieved reps: " + fm.getRep());
        }
    }

    public synchronized void incStoreds(Integer newreps) {
        storeds += newreps;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Integer getChunkN() {
        return chunkN;
    }
}
