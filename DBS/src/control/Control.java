package control;

public class Control {

	public static String ip;
	public static int port;
	private static ControlReceiveThread crt;
	
	public Control(String newip, int newport) {
		ip = newip;
		port = newport;
		
		crt = new ControlReceiveThread(ip,  port);
	}
	
}
