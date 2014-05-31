package space_reclaim;

import main.Chunk;
import main.Database;
import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class SpaceReclaim {
    private final Integer spaceToReclaim;
    private Chunk[] chunks;
    private final boolean delete;

    public SpaceReclaim(Integer newspace) {
        spaceToReclaim = newspace;

        delete = Main.getDatabase().getFreeSpace() < spaceToReclaim;

    }

    public void process() {

        System.out.println("Reclaiming disk space!");

        if (delete) {
            int numberOfChunks = Main.getDatabase().getChunksSize();

            Integer chunksToDelete = (int) Math.ceil((spaceToReclaim - Main.getDatabase().getFreeSpace()) / Main.getChunkSize());

            Database.setDiskSize(Database.getDiskSize() - spaceToReclaim);
            chunks = new Chunk[chunksToDelete];

            Collections.sort(Main.getDatabase().getChunks());

            for (int j = 0; j < chunksToDelete; j++) {
                chunks[j] = Main.getDatabase().getChunk(numberOfChunks - (j + 1));
            }

            for (int j = 0; j < chunksToDelete; j++) {
                FileManager del = new FileManager(chunks[j].getFileId(), 0, true);
                del.deleteChunk(chunks[j].getChunkNo());
                sendMessage(j);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Database.setDiskSize(Database.getDiskSize() - spaceToReclaim);
        }


        System.out.println("Space Reclaimed!");
    }

    private void sendMessage(int j) {
        String msg = "REMOVED " + Main.getVersion() + " " + chunks[j].getFileId() + " " + chunks[j].getChunkNo() +
                Main.getCRLF() + Main.getCRLF();

        Main.getLogger().log("Sent: " + msg);

        Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));

    }
}
