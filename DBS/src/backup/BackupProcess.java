package backup;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.util.ArrayList;

public class BackupProcess extends Thread {

    public String message;
    public ArrayList<String> header;

    public BackupProcess(String newmessage) {
        message = newmessage;
    }

    @Override
    public void run() {

        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
        }

        if (header.get(0).equals("PUTCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                putProcess();
            }
        } else {
            System.out.println("Invalid Message");
        }
    }

    private void putProcess() {

        Chunk chunk = null;
        Boolean found = false;

        for (int i = 0; i < Main.getDatabase().getChunksSize(); i++) {
            chunk = Main.getDatabase().getChunk(i);
            if (chunk.getFileId().equals(header.get(2))) {
                if (chunk.getChunkNo() == Integer.parseInt(header.get(3))) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            FileManager fm = new FileManager(header.get(1), Integer.parseInt(header.get(4)), true);
            fm.writeToFile(Integer.parseInt(header.get(3)), header.get(7).getBytes());
            chunk = new Chunk(header.get(1), Integer.parseInt(header.get(3)), Integer.parseInt(header.get(4)));
            Main.getDatabase().addChunk(chunk);
        }

        String mssg = "STORED" + " " + Main.getVersion() + " " + header.get(1) + " " + header.get(3) + " " + Main
                .getCRLF() + " " + Main.getCRLF();
    }
}
