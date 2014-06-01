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
    private String username;
    private StringBuffer password;

    public RestoreDB(String id, String user, StringBuffer pass) {
        fileId = id;
        username = user;
        password = pass;
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

                if (timeout == 10 || timeout == 20) {
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
            } else if (timeout == 20) {
                Main.getLogger().log("BREAK");
                break;
            }

        } while (true);

        if (restored) {
            Main.getLogger().log("RESTORING");
            FileManager fm = new FileManager(fileId, 0, true);

            Main.getLogger().log("JOINING");
            fm.join(username, password, true);
            Main.getLogger().log("JOINED");
        }

        Main.getLogger().log("RETURN");
        return restored;
    }

    public String getFileId() {
        return fileId;
    }

    public String getUsername() {
        return username;
    }
}
