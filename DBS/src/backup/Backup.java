package backup;

import communication.Communicator;
import main.Main;

import java.util.ArrayList;

public class Backup extends Thread {

    private Communicator mcbComm;
    private ArrayList<BackupSend> sending;

    public Backup(String newip, int newport) {

        sending = new ArrayList<>();
        mcbComm = new Communicator(newip, newport);
    }

    public void run() {
        receive();

    }

    public void receive() {

        while (true) {

            String mssg = mcbComm.receive();

            Main.getService().submit(new BackupProcess(mssg));

        }
    }

    public void send(String message) {

        mcbComm.send(message);
    }

    public void addSending(BackupSend send) {
        sending.add(send);
    }

    public synchronized ArrayList<BackupSend> getSending() {
        return sending;
    }
}
