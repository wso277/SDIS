package restore;

import communication.Communicator;
import main.Main;

public class Restore extends Thread {

    private Communicator mcrComm;

    public Restore(String newip, int newport) {

        mcrComm = new Communicator(newip, newport);
    }

    public void run() {
        receive();

    }

    public void receive() {

        while (true) {

            String mssg = mcrComm.receive();

            Main.getService().submit(new RestoreProcessThread(mssg));
        }
    }

    public void send(String message) {

        mcrComm.send(message);
    }
}
