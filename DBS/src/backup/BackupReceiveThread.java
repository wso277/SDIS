package backup;

import java.net.DatagramPacket;

public class BackupReceiveThread extends Thread {

	public static String ip;
	public static String message;
	public static DatagramPacket packet;
	public static int PSIZE = 64000;
	public static int port;
	public static byte[] rbuf;

	public BackupReceiveThread(String newip, int newport) {
		
	}

}
