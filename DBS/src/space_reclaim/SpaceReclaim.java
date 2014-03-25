package space_reclaim;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.util.Collections;

public class SpaceReclaim {
    private Integer spaceToReclaim;
    private Integer chunksToDelete;
    private Chunk[] chunks;
    private boolean delete;

    public SpaceReclaim(Integer newspace) {
        spaceToReclaim = newspace;

        if (Main.getDatabase().getFreeSpace() >= spaceToReclaim) {
            delete = false;
        } else {
            delete = true;
        }

    }

    public void process() {

        System.out.println("Reclaiming disk space!");

        if (delete == true) {
            int numberOfChunks = Main.getDatabase().getChunksSize();

            chunksToDelete = (int) Math.ceil((spaceToReclaim - Main.getDatabase().getFreeSpace()) / Main.getChunkSize
                    ());

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
        }

        Main.setDiskSize(Main.getDiskSize() - spaceToReclaim);

        System.out.println("Space Reclaimed!");
    }

    private void sendMessage(int j) {
        String mssg = new String("REMOVED " + Main.getVersion() + " " + chunks[j].getFileId() + " " + chunks[j]
                .getChunkNo() + " " + Main.getCRLF() + " " + Main.getCRLF());

        System.out.println("Sent");

        Main.getControl().send(mssg);

    }
}
