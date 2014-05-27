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
        Main.getDatabase().changeRepDegree(fileId, Main.getDatabase().getFileRep(fileId));
        System.out.println(Main.getDatabase().getFileRep(fileId));

        Main.getControl().send(message.getBytes(StandardCharsets.ISO_8859_1));
        System.out.println("Delete message sent!");
        Main.getDatabase().removeFile(fileId);
        System.out.println("File deleted!");
    }

}
