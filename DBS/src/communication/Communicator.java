package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

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

    public byte[] receive() {
        int PSIZE = 65536;
        byte[] buf = new byte[PSIZE];
        DatagramPacket rpacket = new DatagramPacket(buf, PSIZE);

        try {
            socket.receive(rpacket);
        } catch (IOException e) {
            return null;
        }


        try {
            if (rpacket.getAddress().toString().equals("/" + InetAddress.getLocalHost().getHostAddress())) {
                return null;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    /*
    Method to trim de data on a receiving packet found on StackOverflow
     */
        byte[] trimmed_mes = new byte[rpacket.getLength()];
        System.arraycopy(rpacket.getData(), rpacket.getOffset(), trimmed_mes, 0, rpacket.getLength());

        return trimmed_mes;
    }

    public void send(byte[] mssg) {
        DatagramPacket packet;
        packet = new DatagramPacket(mssg, mssg.length, address, port);
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
