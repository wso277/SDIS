package control;

import communication.Communicator;
import main.Main;

public class Control extends Thread {

    private Communicator ctrlComm;

    public Control(String newip, int newport) {

        ctrlComm = new Communicator(newip, newport);
    }

    public void run() {
        System.out.println("Control thread name: " + Thread.currentThread().getName());
        receive();

    }

    public void receive() {

        while (true) {

            String mssg = ctrlComm.receive();

            System.out.println("Received: " + mssg);

            System.out.println("Control thread name: " + Thread.currentThread().getName());

            Main.getService().submit(new ControlProcessThread(mssg));
        }
    }

    public void send(String message) {

        ctrlComm.send(message);
    }

}
