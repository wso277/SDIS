package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import communication.Address;
import delete.Delete;
import main.Main;

public class Cli extends Thread {

	public static BufferedReader in;
	public static String input;

	public Cli() {

		in = new BufferedReader(new InputStreamReader(System.in));

		chooseNetworkConfigType();

		processFirstMenu();
	}

	public void run() {
		System.out.println("Cli thread name: "
				+ Thread.currentThread().getName());
		menu();

	}

	private void chooseNetworkConfigType() {
		input = new String("");
		while (!input.equals("1") && !input.equals("2")) {

			System.out.print("Choose a command:\n"
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
	}

	private void processFirstMenu() {

		String ip = new String("");
		String port = new String("");

		if (input.equals("1")) {
			System.out.print("Insert Control IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print("Insert Control port: ");
			System.out.flush();
			try {
				port = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Address mc = new Address("mc", ip, Integer.parseInt(port));

			System.out.print("Insert Restore IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print("Insert Restore port: ");
			System.out.flush();
			try {
				port = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Address mcr = new Address("mcr", ip, Integer.parseInt(port));

			System.out.print("Insert Backup IP: ");
			System.out.flush();
			try {
				ip = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print("Insert Backup port: ");
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

			System.out.print("\nChoose a command:\n" + "1. Backup a File\n"
					+ "2. Restore a File\n" + "3. Delete a File\n"
					+ "4. Reclaim disk space\n\n" + "Option: ");
			System.out.flush();
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearConsole();
			processInput();
		}
	}

	private void processInput() {
		switch (input) {
		case "1":
		case "backup":
		case "Backup":
		case "BACKUP":
			break;
		case "2":
		case "restore":
		case "Restore":
		case "RESTORE":
			break;
		case "3":
		case "delete":
		case "Delete":
		case "DELETE":
			Main.getDatabase().showBackedUpFiles();
			System.out.print("Choose file to be deleted (number): ");
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String result = Main.getDatabase().getHash(Integer.parseInt(input));
			if (!result.equals("fail")) {
				System.out.println("Sending DELETE message!");
				Main.getService().submit(new Delete(result));
			} else {
				System.out
						.println("Invalid file. Choose one of the availvable numbers!");
			}

			break;
		case "4":
		case "reclaim":
		case "Reclaim":
		case "RECLAIM":
			break;
		default:
			System.out.println("Invalid Option!\n");
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