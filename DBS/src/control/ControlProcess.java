package control;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

class ControlProcess extends Thread {

    private final String message;
    private ArrayList<String> header;

    public ControlProcess(String newmessage) {
        message = newmessage;
    }

    public void run() {

        header = new ArrayList<>();

        String[] tmp;

        tmp = message.split(new String(Main.getCRLF(), StandardCharsets.US_ASCII) + new String(Main.getCRLF(),
                StandardCharsets.US_ASCII));

        String[] tmp1 = tmp[0].split("\\s+");

        for (String aTmp1 : tmp1) {
            header.add(aTmp1.trim());
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
        if (ch != null) {
            FileManager chunk = new FileManager(header.get(2), 0, true);
            chunk.readChunk(Integer.parseInt(header.get(3)));

            String mssg = "CHUNK " + Main.getVersion() + " " + header.get(2) + " " + header.get(3) +
                    new String(Main.getCRLF(), StandardCharsets.US_ASCII) + new String(Main.getCRLF(),
                    StandardCharsets.US_ASCII) + new String(chunk.getChunkData(), StandardCharsets.US_ASCII);

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
    }

    private void storedProcess() {
        for (int j = 0; j < Main.getBackup().getSending().size(); j++) {
            if (Main.getBackup().getSending().get(j).getFileHash().equals(header.get(2))) {
                Main.getBackup().getSending().get(j).setStoreds(1);
                break;
            }
        }

        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            Chunk chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2)) && (chunk.getChunkNo() == Integer.parseInt(header.get(3)))) {
                chunk.setKnownReps(1);
                break;
            }
        }
    }

    private void removedProcess() {
        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            Chunk chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2)) && (chunk.getChunkNo() == Integer.parseInt(header.get(3)))) {
                chunk.setKnownReps(-1);
                break;
            }
        }
    }

    private void deleteProcess() {
        FileManager del = new FileManager(header.get(1), 0, true);
        del.delete();
    }
}
