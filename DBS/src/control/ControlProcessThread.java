package control;

import java.util.ArrayList;

import main.FileManager;
import restore.RestoreProcessThread;

public class ControlProcessThread {

	public static String[] message;
	public static ArrayList<String> header;

	public ControlProcessThread(String newmessage) {
		
		header = new ArrayList<String>();
		
		String[] tmp = newmessage.split("\\s+");

		for (int i = 0; i < tmp.length; i++) {
			header.add(tmp[i].trim());
			System.out.println(header.get(i));
		}
		
	}

	public void process() {

		
		
		if (header.get(0).equals("GETCHUNK")) {
			RestoreProcessThread rpt = new RestoreProcessThread(header.get(2),
					header.get(3));
		} else if (header.get(0).equals("DELETE")) {

			FileManager del = new FileManager(header.get(1), "0", true);
			del.delete();

		} else if (header.get(0).equals("REMOVED")) {

		} else if (header.get(0).equals("STORED")) {

		} else {
			System.err.println("Operation Invalid!");
		}

	}
}
