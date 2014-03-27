package communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Communicator {

    private String ip;
    private Integer port;
    private static int PSIZE = 65536;
    private MulticastSocket socket;
    private InetAddress address;

    public Communicator(String newip, Integer newport) {

        ip = newip;
        port = newport;

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.setTimeToLive(2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        try {
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() throws UnsupportedEncodingException {
        byte[] buf = new byte[PSIZE];
        DatagramPacket rpacket = new DatagramPacket(buf, PSIZE);

        try {
            socket.receive(rpacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(rpacket.getData(), StandardCharsets.US_ASCII);
    }

    public void send(String mssg) {
        DatagramPacket packet = null;
            packet = new DatagramPacket(mssg.getBytes(StandardCharsets.US_ASCII),
                    mssg.getBytes(StandardCharsets.US_ASCII).length, address, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
