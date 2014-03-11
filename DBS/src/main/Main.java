package main;

import java.io.IOException;

import communication.Communicator;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Communicator comm =  new Communicator("230.0.0.5", 7700);
		
		System.out.println("Receiving");
		
		String message = new String();
		
		message = comm.receiveMessage();
		
		System.out.println("Received: " + message);
		
	}

}
