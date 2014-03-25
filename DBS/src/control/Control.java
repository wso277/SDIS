package control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import main.Main;

public class Control extends Thread {

    private static String ip;
    private static int port;
    private static int PSIZE = 65536;
    private static MulticastSocket socket = null;
    private InetAddress address;

    public Control(String newip, int newport) {
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

        System.out.println("IP: " + ip + " PORT: " + port + " ADDRESS: " + address);

    }

    public void run() {
        System.out.println("Control thread name: " + Thread.currentThread().getName());
        receive();

    }

    public void receive() {

        while (true) {
            byte[] buf = new byte[PSIZE];
            DatagramPacket rpacket = new DatagramPacket(buf, PSIZE);

            try {
                socket.receive(rpacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String response = new String(rpacket.getData());

            System.out.println("Received: " + response);

            System.out.println("Control thread name: " + Thread.currentThread().getName());

            Main.getService().submit(new ControlProcessThread(response));
        }
    }

    public void send(String message) {

        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        Control.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Control.port = port;
    }

}
