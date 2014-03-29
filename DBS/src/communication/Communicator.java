package communication;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Communicator {

    private final Integer port;
    private MulticastSocket socket;
    private InetAddress address;

    public Communicator(String newip, Integer newport) {

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
            address = InetAddress.getByName(newip);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        try {
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String receive() {
        int PSIZE = 65536;
        byte[] buf = new byte[PSIZE];
        DatagramPacket rpacket = new DatagramPacket(buf, PSIZE);

        try {
            socket.receive(rpacket);
        } catch (IOException e) {
            return "fail";
        }

        return new String(rpacket.getData(), StandardCharsets.ISO_8859_1);
    }

    public void send(String mssg) {
        DatagramPacket packet;
            packet = new DatagramPacket(mssg.getBytes(StandardCharsets.ISO_8859_1),
                    mssg.getBytes(StandardCharsets.ISO_8859_1).length, address, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }

    public Integer getPort() {
        return port;
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}
