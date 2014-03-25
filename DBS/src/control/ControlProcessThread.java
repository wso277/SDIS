package control;

import java.util.ArrayList;

import main.FileManager;
import main.Main;
import restore.RestoreProcessThread;
import main.Chunk;

public class ControlProcessThread extends Thread {

    public static String message;
    public static ArrayList<String> header;

    public ControlProcessThread(String newmessage) {
        message = newmessage;
    }

    public void run() {

        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
        }

        if (header.get(0).equals("GETCHUNK")) {
            RestoreProcessThread rpt = new RestoreProcessThread(header.get(2),
                    header.get(3));
        } else if (header.get(0).equals("DELETE")) {
            deleteProcess();
            Main.save();

        } else if (header.get(0).equals("REMOVED")) {

            removedProcess();
        } else if (header.get(0).equals("STORED")) {

        } else {
            System.err.println("Operation Invalid!");
        }

    }

    private void removedProcess() {
        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            Chunk chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(1)) && (chunk.getChunkNo() == Integer.parseInt(header.get(2)))) {
                System.out.println("Previous:" + chunk.getChunkNo());
                chunk.setChunkNo(chunk.getChunkNo() - 1);
                System.out.println("After:" + chunk.getChunkNo());
            }
        }
    }

    private void deleteProcess() {
        FileManager del = new FileManager(header.get(1), 0, true);
        del.delete();
    }
}
