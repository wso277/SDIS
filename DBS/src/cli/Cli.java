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

			System.out.println("Enter A Command: ");
			System.out.flush();
			input = in.readLine();
			System.out.println("Entered: " + input);
		}
	}
}
