package control;

import communication.Communicator;
import main.Main;

import java.io.UnsupportedEncodingException;

public class Control extends Thread {

    private Communicator ctrlComm;

    public Control(String newip, int newport) {

        ctrlComm = new Communicator(newip, newport);
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

            String mssg = ctrlComm.receive();
            Main.getService().submit(new ControlProcess(mssg));
        }
    }

    public void send(String message) {

        ctrlComm.send(message);
    }

}
