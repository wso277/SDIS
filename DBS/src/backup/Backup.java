package backup;

import communication.Communicator;

public class Backup extends Thread {

    private Communicator mdbComm;

    public Backup(String newip, int newport) {

        mdbComm = new Communicator(newip, newport);
    }

    public void run() {
        System.out.println("Backup thread name: " + Thread.currentThread().getName());
        receive();

    }

    public void receive() {

        while (true) {

            String mssg = mdbComm.receive();

            System.out.println("Received: " + mssg);

            System.out.println("Control thread name: " + Thread.currentThread().getName());

            //Main.getService().submit(new ControlProcessThread(mssg));
        }
    }

    public void send(String message) {

        mdbComm.send(message);
    }

}
