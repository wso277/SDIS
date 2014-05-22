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
        System.out.println("Nasci.");
    }

    public void run() {

        while (running) {
            Iterator it = Main.getDatabase().getDeletedFiles().entrySet().iterator();
            System.out.println("Hai :D");
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                System.out.println("Correr 1 :D");
                System.out.println((Integer) pairs.getValue());
                if ((Integer) pairs.getValue() > 0) {
                    System.out.println("Correr 2");
                    msg = "DELETE " + (String) pairs.getKey() + Main.getCRLF() + Main.getCRLF();
                    System.out.println(msg);
                    Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));
                    it.remove();
                }
            }

            try {
                System.out.println("Hellooooooooo");
                sleep(1000 * 30); //TODO change to half an hour
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
