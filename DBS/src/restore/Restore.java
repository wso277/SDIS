package restore;

import communication.Communicator;
import main.Main;

import java.io.UnsupportedEncodingException;

public class Restore extends Thread {

    private Communicator mcrComm;

    public Restore(String newip, int newport) {

        mcrComm = new Communicator(newip, newport);
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

            String mssg = mcrComm.receive();

            Main.getService().submit(new RestoreProcess(mssg));
        }
    }

    public void send(String message) {

        mcrComm.send(message);
    }
}
