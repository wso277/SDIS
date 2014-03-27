package backup;

import communication.Communicator;
import main.Main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Backup extends Thread {

    private Communicator mcbComm;
    private ArrayList<BackupSend> sending;

    public Backup(String newip, int newport) {

        sending = new ArrayList<>();
        mcbComm = new Communicator(newip, newport);
    }

    public void run() {
        try {
            receive();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void receive() throws UnsupportedEncodingException {

        while (true) {

            String mssg = mcbComm.receive();
            System.out.println("Mensagem no Backup: ");
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
