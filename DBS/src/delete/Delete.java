package delete;

import main.Main;

import java.util.Random;

public class Delete extends Thread {
    private String message;

    public Delete(String fileId) {
        message = new String("DELETE " + fileId + " " + Main.getCRLF() + " " + Main.getCRLF());
    }

    public void run() {

        Random r = new Random();
        int time;
        for (int i = 0; i < 5; i++) {
            Main.getControl().send(message);
            time = r.nextInt(301) + 200;
            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
