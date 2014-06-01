package restore;

import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;

/**
 * Created by Wilson on 01/06/2014.
 */
public class RestoreDB {
    private Integer waitingConfirmation = -1;
    private String fileId;

    public RestoreDB(String id) {
        fileId = id;
    }

    public void setWaitingConfirmation(Integer newWaitingConfirmation) {
        waitingConfirmation = newWaitingConfirmation;
    }

    public boolean process() {
        int currentChunk = 0;
        int timeout = 0;
        boolean restored = false;

        do {
            String msg = "GETDB " + fileId + " " + currentChunk + Main.getCRLF() + Main.getCRLF();
            waitingConfirmation = -1;

            Main.getLogger().log("Sent: " + msg);

            Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));

            //System.out.println("Waiting for chunk No " + currentChunk);

            restored = false;
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
                if (waitingConfirmation < 64000) {
                    break;
                }
                timeout = 0;
                currentChunk++;
            } else if (timeout == 40) {
                break;
            }

        } while (true);

        if (restored) {
            FileManager fm = new FileManager(Main.getDatabase().getUsername() + "/database.cu", 0, true);
            fm.join();
        }

        return restored;
    }

    public String getFileId() {
        return fileId;
    }
}
