package restore;

import communication.UDPCommunicator;
import main.Main;

import java.io.UnsupportedEncodingException;

public class Restore extends Thread {

    private final UDPCommunicator mcrComm;
    private Boolean running;

    public Restore(String newip, int newport) {

        mcrComm = new UDPCommunicator(newip, newport);
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

            byte[] mssg = mcrComm.receive();

            if (mssg != null) {
                Main.getService().submit(new RestoreProcess(mssg));
            }
        }
    }

    public void send(byte[] message) {

        mcrComm.send(message);
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void close() {
        mcrComm.close();
    }

}
