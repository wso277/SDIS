package backup;

import main.Chunk;
import main.FileManager;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

class BackupProcess extends Thread {

    private final String message;
    private ArrayList<String> header;
    private String body;

    public BackupProcess(String newmessage) {
        message = newmessage;
    }

    @Override
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

        if (header.get(0).equals("PUTCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                //System.out.println("SIZE " + body.length());
                putProcess();
                Main.save();
            }
        } else {
            System.out.println("Invalid Message");
        }
    }

    private void putProcess() {

        Chunk chunk;
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
            FileManager fm = new FileManager(header.get(2), Integer.parseInt(header.get(4)), true);
            fm.writeToFile(Integer.parseInt(header.get(3)), body.getBytes(StandardCharsets.ISO_8859_1));
            chunk = new Chunk(header.get(1), Integer.parseInt(header.get(3)), Integer.parseInt(header.get(4)));
            Main.getDatabase().addChunk(chunk);
        }

        String mssg = "STORED" + " " + Main.getVersion() + " " + header.get(2) + " " + header.get(3) + new String
                (Main.getCRLF(), StandardCharsets.ISO_8859_1) + new String(Main.getCRLF(), StandardCharsets.ISO_8859_1);

        Random r = new Random();
        int time = r.nextInt(401);
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Main.getControl().send(mssg);
    }
}
