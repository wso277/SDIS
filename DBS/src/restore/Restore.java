package restore;

import communication.Communicator;

import java.io.IOException;

public class Restore extends Thread {

    private String ip;
    private Integer port;
    private Communicator rstComm;

    public Restore(String newip, int newport) {
        ip = newip;
        port = newport;

        try {
            rstComm = new Communicator(ip, port);
        } catch (IOException e) {
            System.err.println("Error creating communicator!");
            e.printStackTrace();
        }

    }

    public void send(String message) {

       rstComm.sendMessage(message);
    }
}
