package delete;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Delete {
    private String message;

    public Delete(String fileId) {
        message = "DELETE " + fileId + new String(Main.getCRLF(), StandardCharsets.US_ASCII) + new String(Main
                .getCRLF(), StandardCharsets.US_ASCII);
    }

    public void process() {

        Random r = new Random();
        int time;
        for (int i = 0; i < 5; i++) {
            Main.getControl().send(message);
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
