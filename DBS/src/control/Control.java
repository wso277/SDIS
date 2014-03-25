package control;

import communication.Communicator;
import main.Main;

public class Control extends Thread {

    private Communicator ctrlComm;

    public Control(String newip, int newport) {

        ctrlComm = new Communicator(newip, newport);
    }

    public void run() {
        receive();

    }

    public void receive() {

        while (true) {

            String mssg = ctrlComm.receive();

            Main.getService().submit(new ControlProcessThread(mssg));
        }
    }

    public void send(String message) {

        ctrlComm.send(message);
    }

}
