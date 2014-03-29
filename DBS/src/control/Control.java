package control;

import communication.Communicator;
import main.Main;

import java.io.UnsupportedEncodingException;

public class Control extends Thread {

    private final Communicator ctrlComm;
    private Boolean running;

    public Control(String newip, int newport) {

        ctrlComm = new Communicator(newip, newport);
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

            byte[] mssg = ctrlComm.receive();
            //if (!mssg.equals("fail")) {
                Main.getService().submit(new ControlProcess(mssg));
            //}
        }
    }

    public void send(byte[] message) {

        ctrlComm.send(message);
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void close() {
        ctrlComm.close();
    }
}
