package restore;

import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;

public class RestoreSend {
    private static Integer waitingConfirmation;
    private String fileId;

    public RestoreSend(String id) {
        fileId = id;
    }

    public static Integer getWaitingConfirmation() {
        return waitingConfirmation;
    }

    public static void setWaitingConfirmation(Integer waitingConfirmation) {
        RestoreSend.waitingConfirmation = waitingConfirmation;
    }

    public void process() {
        int currentChunk = 1;

        do {
            String msg = "GETCHUNK " + Main.getVersion() + " " + fileId +
                    " " + currentChunk + new String(Main.getCRLF(), StandardCharsets.US_ASCII) + new String(Main
                    .getCRLF(), StandardCharsets.US_ASCII);
            waitingConfirmation = -1;

            Main.getControl().send(msg);

            System.out.println("Waiting for chunk No " + currentChunk);

            synchronized (waitingConfirmation) {
                try {
                    waitingConfirmation.wait();
                } catch (InterruptedException e) {
                    // Happens if someone interrupts your thread.
                }
            }

            currentChunk++;
        } while (waitingConfirmation == Main.getChunkSize());

        FileManager fm = new FileManager(fileId, 0, true);
        fm.join();

        System.out.println("Restore Complete!");
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
