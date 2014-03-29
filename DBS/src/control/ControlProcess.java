package control;

import backup.BackupSend;
import main.Chunk;
import main.FileManager;
import main.Main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

class ControlProcess extends Thread {

    private final byte[] message;
    private ArrayList<String> header;

    public ControlProcess(byte[] newmessage) {
        message = newmessage;
    }

    public void run() {

        header = new ArrayList<>();

        String tmp;
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(message)));

        tmp = "";
        try {
            tmp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] tmp1 = tmp.split("\\s+");

        for (String aTmp1 : tmp1) {
            header.add(aTmp1.trim());
        }

        if (header.get(0).equals("GETCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                getChunkProcess();
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

    private void getChunkProcess() {
        Chunk ch = null;
        Boolean found = false;
        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            ch = Main.getDatabase().getChunk(i);
            if (ch.getFileId().equals(header.get(2)) && (ch.getChunkNo() == Integer.parseInt(header.get(3)))) {
                found = true;
                break;
            }
        }

        if (found) {
            FileManager chunk = new FileManager(header.get(2), 0, true);
            chunk.readChunk(Integer.parseInt(header.get(3)));

            String message = "CHUNK " + Main.getVersion() + " " + header.get(2) + " " + header.get(3) +
                    Main.getCRLF() + Main.getCRLF();

            byte[] mssg = message.getBytes(StandardCharsets.ISO_8859_1);
            byte[] mssg1 = Main.appendArray(mssg, chunk.getChunkData());

            System.out.println("Message Size: " + mssg.length);

            Random r = new Random();
            int time = r.nextInt(401);
            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (!ch.getSent()) {
                Main.getRestore().send(mssg1);
            } else {
                ch.setSent(false);
            }
        }
    }

    private void storedProcess() {
        for (int j = 0; j < Main.getBackup().getSending().size(); j++) {
            if (Main.getBackup().getSending().get(j).getFileHash().equals(header.get(2))) {
                Main.getBackup().getSending().get(j).incStoreds(1);
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
                if (chunk.getKnownReps() < chunk.getRepDegree()) {
                    BackupSend send = new BackupSend(chunk.getFileId(), chunk.getRepDegree(), false, chunk.getChunkNo());
                    Main.getBackup().addSending(send);
                    Main.getService().submit(send);
                }
                break;
            }

        }

    }

    private void deleteProcess() {
        FileManager del = new FileManager(header.get(1), 0, true);
        del.delete();
    }
}
