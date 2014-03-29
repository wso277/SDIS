package restore;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class RestoreProcess extends Thread {

    private final byte[] message;
    private ArrayList<String> header;
    private byte[] body;

    public RestoreProcess(byte[] newmessage) {
        message = newmessage;
    }

    public void run() {
        header = new ArrayList<>();

        String tmp;
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(message)));

        tmp ="";
        try {
            tmp = in.readLine();

            System.out.println(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] tmp1 = tmp.split("\\s+");

        for (String aTmp1 : tmp1) {
            header.add(aTmp1.trim());
        }

        body = new byte[tmp.length() - tmp.length() + 4];
        for (int i = tmp.length() + 4, j = 0; i < message.length; i++, j++) {
            body[j] = message[i];
        }

        if (header.get(0).equals("CHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                restoreChunkProcess();
                Main.save();
            }
        }
    }

    private void restoreChunkProcess() {

        Chunk chunk = null;
        Boolean found = false;
        Boolean repFound = false;
        Integer repDegree = 2;

        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2))) {

                if (!repFound) {
                    repDegree = chunk.getRepDegree();
                    repFound = true;
                }

                if (chunk.getChunkNo() == Integer.parseInt(header.get(3))) {
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            chunk.setSent(true);
        } else {
            FileManager fm = new FileManager(header.get(2), repDegree, true);
            fm.writeToFile(Integer.parseInt(header.get(3)), body);
            chunk = new Chunk(header.get(2), Integer.parseInt(header.get(3)), repDegree);
            Main.getDatabase().addChunk(chunk);
            Main.getRestoring().setWaitingConfirmation(body.length);
        }
    }
}
