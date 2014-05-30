package delete;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by João on 22/05/2014.
 */
public class DeleteProcess extends Thread {
    private String msg;
    private boolean running;

    public DeleteProcess() {
        msg = null;
        running = true;
    }

    public void run() {

        while (running) {
            Iterator it = Main.getDatabase().getDeletedFiles().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                if ((Integer) pairs.getValue() > 0) {
                    msg = "DELETE " + (String) pairs.getKey() + Main.getCRLF() + Main.getCRLF();
                    System.out.println("Sleep is over! Sent another DELETE message!");
                    Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));
                    it.remove();
                }
            }

            try {
                sleep(1000 * 60 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
