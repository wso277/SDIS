package delete;

import main.Main;

public class Delete extends Thread {
	String message;
	public Delete(String fileId) {
		message = new String("DELETE " + fileId + " " + Main.getCRLF());
	}
	
	public void run() {
		Main.getControl().send(message);
		try {
			sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.getControl().send(message);
		try {
			sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.getControl().send(message);
		try {
			sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.getControl().send(message);
	}
	
}
