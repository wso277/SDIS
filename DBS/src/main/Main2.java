package main;

import java.io.IOException;

import communication.Communicator;

public class Main2 {

	public static void main(String[] args) throws IOException {
		
		Communicator comm =  new Communicator("230.0.0.5", 7700);
		
		String message = new String("Hello World!");
		
		comm.sendMessage(message);
		
		System.out.println("Sent!");
		
	}

}
