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

    public BackupSend(String filePath, Integer repDegree) {

        fm = new FileManager(filePath, repDegree, false);
        fileHash = fm.getHashString().toString();

        time = 500;
        storeds = 0;
        tries = 0;
    }

    public void run() {

        Boolean enoughSpace;
        enoughSpace = fm.split();

        if (enoughSpace) {
            Integer chunkNo = 0;

            while (fm.readChunk(chunkNo)) {

                String message = "";
                    message = "PUTCHUNK " + Main.getVersion() + " " + fileHash + " " + chunkNo + " " + fm.getRep() +
                            new String(Main.getCRLF(), StandardCharsets.ISO_8859_1) + new String(Main.getCRLF(),
                            StandardCharsets.ISO_8859_1) + new String(fm.getChunkData(), StandardCharsets.ISO_8859_1);

                //System.out.println("SENT : " + new String(fm.getChunkData(), StandardCharsets.ISO_8859_1).length());
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
    }

    public synchronized void setStoreds(Integer newreps) {
        storeds += newreps;
    }

    public String getFileHash() {
        return fileHash;
    }
}
