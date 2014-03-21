package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cli {

	public static BufferedReader in;
	public static String input;

	public Cli() {

	}

	public static void run() throws IOException {
		input = new String("");
		while (!input.equals("exit")) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("Choose a command:\n" + "1. Backup a File\n"
					+ "2. Restore a File\n" + "3. Delete a File\n"
					+ "4. Reclaim disk space\n\n" + "Option: ");
			System.out.flush();
			input = in.readLine();
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