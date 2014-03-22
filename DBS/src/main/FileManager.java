package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

	private static File file;
	private static long fileSize;
	private static InputStream in;
	private static OutputStream out;
	private static byte[] chunkData;
	private static byte[] hashFileName;
	private static String fileName;
	private static Integer rep;
	private static StringBuffer hashString;

	public FileManager(String newfileName, Integer newrep, Boolean load) {

		if (load) {
			hashString = new StringBuffer(newfileName);
			fileName = newfileName;
		} else {
			fileName = newfileName;
			encodeName();
		}
		rep = newrep;

	}

	public boolean deleteChunk(Integer chunkNo) {

		File chunk = new File(hashString.toString() + "/" + chunkNo + ".part");

		if (chunk.exists()) {

			chunk.delete();
			return true;
		}

		return false;
	}

	public void delete() {

		int chunkNo = 1;

		while (true) {

			if (deleteChunk(chunkNo)) {
				chunkNo++;

			} else {
				break;
			}
		}

		File folder = new File(hashString.toString());

		folder.delete();

		Main.getDatabase().removeFile(hashString.toString());
	}
	
	public boolean readChunk(Integer chunkNo) {
		
		File chunk = new File(hashString.toString() + "/" + chunkNo
				+ ".part");

		if (chunk.exists()) {

			try {
				in = new BufferedInputStream(new FileInputStream(chunk));
			} catch (FileNotFoundException e) {
				System.err.println("Error creating input stream");
				e.printStackTrace();
			}

			chunkData = new byte[Main.getChunkSize()];

			try {
				in.read(chunkData);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		return false;
	}

	public void join() {
		int chunkNo = 1;

		while (true) {

				if (readChunk(chunkNo)) {

				writeToFile(0, chunkData);

				chunkNo++;

			} else {
				break;
			}
		}
	}

	public void split() {
		int totalBytesRead = 0;
		int bytesRead = 0;
		int chunkNo = 0;

		// Opens a file to split
		file = new File(fileName);

		fileSize = file.length();

		Integer chunksToBeCreated = (int) Math.ceil(fileSize
				/ Main.getChunkSize());

		if (Main.getDatabase().getFreeSpace() >= chunksToBeCreated
				* Main.getChunkSize()) {

			try {
				in = new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				System.err.println("Error creating input stream");
				e.printStackTrace();
			}

			while (totalBytesRead < fileSize) { // verify filesize multiple of
												// 64kb
												// and and chunk

				chunkData = new byte[Main.getChunkSize()];

				try {
					bytesRead = in.read(chunkData, 0, Main.getChunkSize());
				} catch (IOException e) {
					System.err.println("Error reading stream");
					e.printStackTrace();
				}

				if (bytesRead >= 0) {
					totalBytesRead += bytesRead;
					chunkNo++;
					Chunk chunk = new Chunk(hashString.toString(), chunkNo, rep);

					writeToFile(chunkNo, chunkData);

					Main.getDatabase().addChunk(chunk);
					System.err.println("read");
				} else {
					System.err.println("Error reading file BytesRead: "
							+ bytesRead);
					break;
				}

			}

			Main.getDatabase().addFile(hashString.toString(), fileName);

		} else {
			System.err.println("Not enough space to backup file");
		}
	}

	private void writeToFile(int chunkNo, byte[] data) {
		File dir = new File(hashString.toString());

		if (!dir.exists()) {
			Boolean result = dir.mkdir();

			if (!result) {
				System.err.println("Error creating folder!");
			}
		}

		File newFile = null;

		if (chunkNo != 0) {
			newFile = new File(new String(hashString.toString() + "/" + chunkNo
					+ ".part"));
		} else {
			newFile = new File(new String(Main.getDatabase().getFile(
					hashString.toString())));
		}

		if (!newFile.exists()) {
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Error creating file!");
				e.printStackTrace();
			}
		}

		try {
			out = new BufferedOutputStream(new FileOutputStream(newFile, true));
		} catch (FileNotFoundException e) {
			System.err.println("Error creating output stream");
			e.printStackTrace();
		}

		try {
			out.write(data);
		} catch (IOException e) {
			System.err.println("Error writing chunk to file");
			e.printStackTrace();
		}
	}

	private void encodeName() {

		File tmp = new File(fileName);

		String strTmp = new String(fileName + tmp.lastModified());

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		try {
			hashFileName = digest.digest(strTmp.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		hashString = new StringBuffer();
		for (int i = 0; i < hashFileName.length; i++) {
			hashString.append(Integer.toString(
					(hashFileName[i] & 0xff) + 0x100, 16).substring(1));
		}

	}

}
