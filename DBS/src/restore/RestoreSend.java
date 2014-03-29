package restore;

import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;

public class RestoreSend {
    private Integer waitingConfirmation = -1;
    private String fileId;

    public RestoreSend(String id) {
        fileId = id;
    }

    public Integer getWaitingConfirmation() {
        return waitingConfirmation;
    }

    public void setWaitingConfirmation(Integer newwaitingConfirmation) {
        waitingConfirmation = newwaitingConfirmation;
    }

    public void process() {
        int currentChunk = 0;

        do {
            String msg = "GETCHUNK " + Main.getVersion() + " " + fileId +
                    " " + currentChunk + new String(Main.getCRLF(), StandardCharsets.ISO_8859_1) + new String(Main
                    .getCRLF(), StandardCharsets.ISO_8859_1);
            waitingConfirmation = -1;

            Main.getControl().send(msg);

            System.out.println("Waiting for chunk No " + currentChunk);

            while (waitingConfirmation == -1) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Received " + currentChunk);

            currentChunk++;
        } while (waitingConfirmation == Main.getChunkSize());

        System.out.println("Exited receive");

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
