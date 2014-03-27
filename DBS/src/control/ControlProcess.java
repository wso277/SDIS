package control;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.util.ArrayList;
import java.util.Random;

public class ControlProcess extends Thread {

    public String message;
    public ArrayList<String> header;

    public ControlProcess(String newmessage) {
        message = newmessage;
    }

    public void run() {

        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
        }

        if (header.get(0).equals("GETCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                getchunkProcess();
            }
        } else if (header.get(0).equals("DELETE")) {
            deleteProcess();
            Main.save();

        } else if (header.get(0).equals("REMOVED")) {
            if (Main.getVersion().equals(header.get(1))) {
                removedProcess();
                Main.save();
            }
        } else if (header.get(0).equals("STORED")) {
            if (Main.getVersion().equals(header.get(1))) {
                storedProcess();
                Main.save();
            }

        } else {
            System.err.println("Operation Invalid!");
        }

    }

    private void getchunkProcess() {
        Chunk ch = null;

        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            ch = Main.getDatabase().getChunk(i);
            if (ch.getFileId().equals(header.get(2)) && (ch.getChunkNo() == Integer.parseInt(header.get(3)))) {
                break;
            }
        }

        FileManager chunk = new FileManager(header.get(2), 0, true);
        chunk.readChunk(Integer.parseInt(header.get(3)));

        String mssg = new String("CHUNK " + Main.getVersion() + " " + header.get(2) + " " + header.get(3) + " " +
                Main.getCRLF() + " " + Main.getCRLF() + chunk.getChunkData());

        Random r = new Random();
        int time = r.nextInt(401);
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!ch.getSent()) {
            Main.getRestore().send(mssg);
        } else {
            ch.setSent(false);
        }
    }

    private void storedProcess() {
        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            Chunk chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2)) && (chunk.getChunkNo() == Integer.parseInt(header.get(3)))) {
                chunk.setKnowReps(1);
                break;
            }
        }
    }

    private void removedProcess() {
        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            Chunk chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2)) && (chunk.getChunkNo() == Integer.parseInt(header.get(3)))) {
                chunk.setKnowReps(-1);
                break;
            }
        }
    }

    private void deleteProcess() {
        FileManager del = new FileManager(header.get(1), 0, true);
        del.delete();
    }
}
