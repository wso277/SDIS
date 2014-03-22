package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import communication.Address;

import main.Main;

public class Cli extends Thread {

	public static BufferedReader in;
	public static String input;

	public Cli() {

		input = new String("");
		while (!input.equals("1") && !input.equals("2")) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			System.out
					.println("Choose a command:\n"
							+ "1. Insert new network configurations\n"
							+ "2. Load existing network configurations\n\n"
							+ "Option: ");
			System.out.flush();
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearConsole();
		}
		
		processFirstMenu();
		
		menu();
	}
	
	private void processFirstMenu() {
		
		String ip = new String("");
		String port = new String("");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		
		if (input.equals("1")) {
			System.out.println("Insert Control IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Insert Control port: ");
			System.out.flush();
			try {
				port = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Address mc = new Address("mc", ip, Integer.parseInt(port));
			
			System.out.println("Insert Restore IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Insert Restore port: ");
			System.out.flush();
			try {
				port = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Address mcr = new Address("mcr", ip, Integer.parseInt(port));
			
			System.out.println("Insert Backup IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Insert Backup port: ");
			System.out.flush();
			try {
				port = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Address mcb = new Address("mcb", ip, Integer.parseInt(port));
			
			Main.saveNetwork(mc, mcr, mcb);
			
		}
		
		Main.loadNetwork();
	}

	public void menu() {
		input = new String("");
		while (!input.equals("exit")) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("Choose a command:\n" + "1. Backup a File\n"
					+ "2. Restore a File\n" + "3. Delete a File\n"
					+ "4. Reclaim disk space\n\n" + "Option: ");
			System.out.flush();
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearConsole();
		}
	}

	private static void clearConsole() {
		try {
			String os = System.getProperty("os.name");

			if (os.contains("Windows")) {
				Runtime.getRuntime().exec("cls");
			} else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (Exception exception) {
			// Handle exception.
		}
	}
}