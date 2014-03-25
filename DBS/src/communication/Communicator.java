package communication;

import java.io.IOException;
import java.net.*;

public class Communicator {
	private static int PSIZE = 65536;
	private static MulticastSocket socket = null;
	private static InetAddress address;
	private static DatagramPacket packet = null;
	private static DatagramPacket rpacket = null;
	private static String ip;
	private static int port;
	private static byte[] buf;

	public Communicator(String newip, int newport) throws IOException {
		
		
		ip = newip;
		port = newport;

		try {
			socket = new MulticastSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		socket.setReuseAddress(true);
/*
		SocketAddress socketA = new InetSocketAddress(port);
		
		socket.bind(socketA);*/
		socket.setTimeToLive(2);
		
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		socket.joinGroup(address);

        System.out.println("IP: " + ip + " PORT: " + port + " ADDRESS: " + address);

	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Communicator.ip = ip;
	}

	public static MulticastSocket getSocket() {
		return socket;
	}

	public static void setSocket(MulticastSocket socket) {
		Communicator.socket = socket;
	}

	public static InetAddress getAddress() {
		return address;
	}

	public static void setAddress(InetAddress address) {
		Communicator.address = address;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Communicator.port = port;
	}

	public void joinGroup() throws IOException {

		socket.joinGroup(address);

		socket.setTimeToLive(2);

	}

	public void leaveGroup() {

		socket.close();

	}

	public synchronized void sendMessage(String message) {

		packet = new DatagramPacket(message.getBytes(),
				message.getBytes().length, address, port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String receiveMessage() {
		buf = new byte[PSIZE];
		rpacket = new DatagramPacket(buf, PSIZE);

		try {
			socket.receive(rpacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String response = new String(rpacket.getData());
        System.out.println("Received: " + response);

        System.out.println("Control thread name: "
                + Thread.currentThread().getName());
		return response;

	}

}
