package backup;

import communication.Communicator;
import main.Main;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;

public class Backup extends Thread {

    private final Communicator mcbComm;
    private final ArrayList<BackupSend> sending;
    private Boolean running;

    public Backup(String newip, int newport) throws SocketException {

        sending = new ArrayList<>();
        mcbComm = new Communicator(newip, newport);
        running = true;
    }

    public void run() {
        try {
            receive();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    void receive() throws UnsupportedEncodingException {

        while (running) {

            byte[] mssg = mcbComm.receive();
            if (mssg != null) {

                Main.getService().submit(new BackupProcess(mssg));
            }

        }
    }

    public void send(byte[] message) {

        mcbComm.send(message);
    }

    public void addSending(BackupSend send) {
        sending.add(send);
    }

    public synchronized ArrayList<BackupSend> getSending() {
        return sending;
    }

    public void close() {
        mcbComm.close();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
