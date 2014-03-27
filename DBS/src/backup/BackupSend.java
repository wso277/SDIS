package backup;

import main.FileManager;
import main.Main;

import java.io.UnsupportedEncodingException;

public class BackupSend extends Thread {
    private String message;
    private String fileHash;
    private FileManager fm;
    private Boolean enoughSpace;
    private Integer time;
    private Integer storeds;
    private Integer tries;

    public BackupSend(String filePath, Integer repDegree) {

        fm = new FileManager(filePath, repDegree, false);
        fileHash = fm.getHashString().toString();

        time = 500;
        storeds = 0;
        tries = 0;
    }

    public void run() {

        if (fm.split()) {
            enoughSpace = true;
        } else {
            enoughSpace = false;
        }

        if (enoughSpace) {
            Integer chunkNo = 1;

            while (fm.readChunk(chunkNo)) {

                message = "";
                try {
                    message = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                            new String(Main.getCRLF(), "UTF-8") + new String(Main.getCRLF(),
                            "UTF-8") + " " + new String(fm.getChunkData(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                chunkNo++;

                while (storeds < fm.getRep() && tries < 5) {

                    storeds = 0;
                    Main.getBackup().send(message);


                    try {
                        sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tries++;
                    time += time;
                }

                storeds = 0;
                time = 500;
                tries = 0;
            }
        } else {
            System.out.println("Not enough space for backup!");
        }
    }

    public synchronized void setStoreds(Integer newreps) {
        storeds += newreps;
    }

    public String getFileHash() {
        return fileHash;
    }
}
