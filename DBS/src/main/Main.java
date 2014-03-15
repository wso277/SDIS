package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import backup.Backup;
import communication.Communicator;



public class Main {
	private static Backup backup;
	private static HashMap<String, String> ipData;
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		
		
		backup = new Backup(args[2], Integer.parseInt(args[3]));
		
	}
	
	private static void processMessage(String message) {
		
		
		
	}

}
