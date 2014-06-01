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

    public void setWaitingConfirmation(Integer newWaitingConfirmation) {
        waitingConfirmation = newWaitingConfirmation;
    }

    public void process() {
        int currentChunk = 0;
        FileManager fm;

        do {
            String msg = "GETCHUNK " + Main.getVersion() + " " + fileId +
                    " " + currentChunk + Main.getCRLF() + Main.getCRLF();
            waitingConfirmation = -1;

            Main.getLogger().log("Sent: " + msg);

            Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));

            System.out.println("Waiting for chunk No " + currentChunk);

            int timeout = 0;
            boolean restored = false;
            while (waitingConfirmation == -1) {
                timeout++;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (timeout == 20) {
                    break;
                }
            }

            if (waitingConfirmation != -1) {
                restored = true;
            }

            if (restored) {

                fm = new FileManager(fileId, 0, true);

                System.out.println("Received " + currentChunk + " with size " + fm.getChunkSize(currentChunk));

                if (fm.getChunkSize(currentChunk) < 64000) {
                    break;
                }

                currentChunk++;
            }

        } while (true);

        System.out.println("Exited receive");

        fm = new FileManager(fileId, 0, true);
        fm.join();

        System.out.println("Restore Complete!");
    }

    public String getFileId() {
        return fileId;
    }

}
