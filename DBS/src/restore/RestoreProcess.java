package restore;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.util.ArrayList;

public class RestoreProcess extends Thread {

    public String message;
    public ArrayList<String> header;

    public RestoreProcess(String newmessage) {
        message = newmessage;
    }

    public void run() {
        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
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
            FileManager fm = new FileManager(header.get(1), repDegree, true);
            fm.writeToFile(Integer.parseInt(header.get(3)), header.get(6).getBytes());
            chunk = new Chunk(header.get(1), Integer.parseInt(header.get(3)), repDegree);
            Main.getDatabase().addChunk(chunk);
        }
    }
}
