package restore;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class RestoreProcess extends Thread {

    private final String message;
    private ArrayList<String> header;
    private String body;

    public RestoreProcess(String newmessage) {
        message = newmessage;
    }

    public void run() {
        header = new ArrayList<>();

        String[] tmp;

        tmp = message.split(new String(Main.getCRLF(), StandardCharsets.ISO_8859_1) + new String(Main.getCRLF(),
                StandardCharsets.ISO_8859_1));

        String[] tmp1 = tmp[0].split("\\s+");
        body = tmp[1].trim();

        for (String aTmp1 : tmp1) {
            header.add(aTmp1.trim());
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
            fm.writeToFile(Integer.parseInt(header.get(3)), body.getBytes(StandardCharsets.ISO_8859_1));
            chunk = new Chunk(header.get(1), Integer.parseInt(header.get(3)), repDegree);
            Main.getDatabase().addChunk(chunk);
            synchronized (RestoreSend.getWaitingConfirmation()) {
                RestoreSend.setWaitingConfirmation(header.get(6).getBytes(StandardCharsets.ISO_8859_1).length);
                RestoreSend.getWaitingConfirmation().notify();
            }
        }
    }
}
