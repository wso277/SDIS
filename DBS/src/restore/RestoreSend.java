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
        FileManager fm;

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

            fm = new FileManager(fileId, 0, true);

            System.out.println("Received " + currentChunk + " with size " + fm.getChunkSize(currentChunk));

            if(fm.getChunkSize(currentChunk) < 64000){
                break;
            }

            currentChunk++;
        } while (true);

        System.out.println("Exited receive");

        fm = new FileManager(fileId, 0, true);
        fm.join();

        System.out.println("Restore Complete!");
    }

}
