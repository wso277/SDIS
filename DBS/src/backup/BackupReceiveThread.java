package backup;

import java.net.DatagramPacket;

public class BackupReceiveThread extends Thread {

    public String ip;
    public String message;
    public DatagramPacket packet;
    public int PSIZE = 64000;
    public int port;
    public byte[] rbuf;

    public BackupReceiveThread(String newip, int newport) {

    }

}
