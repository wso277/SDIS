package backup;

import communication.Communicator;

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

            System.out.println("Received: " + mssg);

            System.out.println("Control thread name: " + Thread.currentThread().getName());

            //Main.getService().submit(new ControlProcessThread(mssg));
        }
    }

    public void send(String message) {

        mcbComm.send(message);
    }

}
