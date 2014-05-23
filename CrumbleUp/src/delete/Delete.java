package delete;

import main.Chunk;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class Delete {
    private final String message;
    private String fileId;

    public Delete(String fileId) {
        this.fileId = fileId;
        message = "DELETE " + fileId + Main.getCRLF() + Main.getCRLF();
    }

    public void process() {
        int maxRep = 0;

        Collections.sort(Main.getDatabase().getChunks());

        for (int i = Main.getDatabase().getChunks().size() - 1; i >= 0; i--) {
            Chunk c = Main.getDatabase().getChunk(i);
            if (c.getFileId().equals(fileId)) {
                if (c.getKnownReps() > maxRep) {
                    maxRep = c.getKnownReps();
                    System.out.println(maxRep);
                }
            }
        }

        Main.getDatabase().changeRepDegree(fileId, maxRep);
        System.out.println(maxRep);

        Main.getControl().send(message.getBytes(StandardCharsets.ISO_8859_1));
        System.out.println("Delete message sent!");
    }

}
