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
        int max_rep = 0;

        Collections.sort(Main.getDatabase().getChunks());

        for (int i = Main.getDatabase().getChunks().size() - 1; i >= 0; i--) {
            Chunk c = Main.getDatabase().getChunk(i);
            if (c.getFileId() == fileId) {
                if (c.getKnownReps() > max_rep) {
                    max_rep = c.getKnownReps();
                }
            }
        }

        Main.getDatabase().changeRepDegree(fileId, max_rep);

        Main.getControl().send(message.getBytes(StandardCharsets.ISO_8859_1));
        System.out.println("Delete message sent!");
    }

}
