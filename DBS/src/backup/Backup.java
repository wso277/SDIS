package backup;

import communication.Communicator;
import main.Main;

public class Backup extends Thread {

    private Communicator mcbComm;

    public Backup(String newip, int newport) {

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

}
