package space_reclaim;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by João on 27/05/2014.
 */
public class ReclaimProcess {
    private String msg;
    private boolean running;

    public ReclaimProcess() {
        msg = null;
        running = true;
    }

    public void run() {
        while(running) {
            //TODO COMPOR ISTO
            Iterator it = Main.getDatabase().getDeletedFiles().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                msg = "PUTCHUNK " + (String) pairs.getKey() + Main.getCRLF() + Main.getCRLF();
                System.out.println("Sleep is over! Sent another DELETE message!");
                Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));
                it.remove();
            }
        }
    }
}
