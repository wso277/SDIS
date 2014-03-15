package communication;

public class Address {
	private String ip;
	private int port;
	
	public Address(String newip, int newport) {
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
