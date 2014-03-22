package communication;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String ip;
	private int port;
	
	public Address(String newname, String newip, int newport) {
		name = newname;
		ip = newip;
		port = newport;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String newip) {
		ip = newip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int newport) {
		port = newport;
	}
}
