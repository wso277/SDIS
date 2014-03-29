package delete;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Delete {
    private final String message;

    public Delete(String fileId) {
        message = "DELETE " + fileId + Main.getCRLF() + Main.getCRLF();
    }

    public void process() {

        Random r = new Random();
        int time;
        for (int i = 0; i < 5; i++) {
            Main.getControl().send(message.getBytes(StandardCharsets.ISO_8859_1));
            time = r.nextInt(301) + 200;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Delete message sent!");
    }

}
