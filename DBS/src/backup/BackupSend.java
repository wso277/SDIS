package backup;

import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;

public class BackupSend extends Thread {
    private final String fileHash;
    private final FileManager fm;
    private Integer time;
    private Integer storeds;
    private Integer tries;
    private Integer chunkN;
    private Boolean isFile;

    public BackupSend(String filePath, Integer repDegree, Boolean file, Integer newChunkNo) {

        fm = new FileManager(filePath, repDegree, false);
        fileHash = fm.getHashString().toString();

        time = 500;
        storeds = 0;
        tries = 0;
        isFile = file;
        chunkN = newChunkNo;
    }

    public void run() {

        if (isFile) {
            Boolean enoughSpace;
            enoughSpace = fm.split();

            if (enoughSpace) {
                Integer chunkNo = 0;

                while (fm.readChunk(chunkNo)) {

                    String message = "";
                    message = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                            Main.getCRLF() + Main.getCRLF();

                    byte[] mssg = message.getBytes(StandardCharsets.ISO_8859_1);
                    byte[] mssg1 = Main.appendArray(mssg, fm.getChunkData());

                    while (storeds < fm.getRep() && tries < 5) {

                        storeds = 0;
                        Main.getBackup().send(mssg1);

                        try {
                            sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        tries++;
                        time += time;
                    }

                    fm.deleteChunk(chunkNo);

                    chunkNo++;
                    storeds = 0;
                    time = 500;
                    tries = 0;
                }
            } else {
                System.out.println("Not enough space for backup!");
            }

            System.out.println("Finished backup of fileid " + fileHash);
        } else {
            backupChunk(chunkN);
        }
    }

    public void backupChunk(Integer chunkNo) {
        fm.readChunk(chunkNo);
        String message = "";
        message = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                Main.getCRLF() + Main.getCRLF();

        byte[] mssg = message.getBytes(StandardCharsets.ISO_8859_1);
        byte[] mssg1 = Main.appendArray(mssg, fm.getChunkData());

        while (storeds < fm.getRep() && tries < 5) {

            storeds = 0;
            Main.getBackup().send(mssg1);


            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tries++;
            time += time;
        }

        fm.deleteChunk(chunkNo);

        chunkNo++;
        storeds = 0;
        time = 500;
        tries = 0;
    }

    public synchronized void incStoreds(Integer newreps) {
        storeds += newreps;
    }

    public String getFileHash() {
        return fileHash;
    }

    public FileManager getFm() {
        return fm;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getStoreds() {
        return storeds;
    }

    public void setStoreds(Integer storeds) {
        this.storeds = storeds;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }
}
