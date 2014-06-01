package backup;

import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;

/**
 * Created by Wilson on 01/06/2014.
 */
public class BackupDB extends Thread {

    private String dbId;

    public BackupDB() {
        dbId = Main.getDatabase().getDbId().toString();
    }

    @Override
    public void run() {

        FileManager fm = new FileManager(dbId, -1, true);
        fm.splitDb(Main.getDatabase().getUsername() + "/database.cu", dbId);

        Integer chunkNo = 0;

        while (fm.readChunk(chunkNo, Main.getDatabase().getUsername())) {

            String msg;
            msg = "PUTDB " + " " + dbId + " " + chunkNo + Main.getCRLF() + Main.getCRLF();

            Main.getLogger().log("Sent: " + msg);

            byte[] msgBytes = msg.getBytes(StandardCharsets.ISO_8859_1);
            byte[] msg1 = Main.appendArray(msgBytes, fm.getChunkData());

            Main.getBackup().send(msg1);

            fm.deleteChunk(chunkNo);
            chunkNo++;
        }

    }

}
