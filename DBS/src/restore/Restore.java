package restore;

import java.io.IOException;

public class Restore extends Thread {

    private String ip;
    private Integer port;

    public Restore(String newip, int newport) {
        ip = newip;
        port = newport;

    }

    public void send(String message) {

    }
}
