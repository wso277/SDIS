package backup;

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

class BackupProcess extends Thread {

    private final byte[] message;
    private ArrayList<String> header;
    private byte[] body;

    public BackupProcess(byte[] newMessage) {
        message = newMessage;
    }

    @Override
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

        body = new byte[message.length - 8 - tmp.length() + 4];

        for (int i = tmp.length() + 4, j = 0; i < message.length - 8; i++, j++) {
            body[j] = message[i];
        }

        if (header.get(0).equals("PUTCHUNK")) {
            if (Main.getVersion().equals(header.get(1))) {
                putProcess();
                Main.save();
            }
        } else {
            System.out.println("Invalid Message");
        }
    }

    private void putProcess() {

        for (int i = 0; i < Main.getBackup().getSending().size(); i++) {
            if (Main.getBackup().getSending().get(i).getFileHash().equals(header.get(2)) && Main.getBackup()
                    .getSending().get(i).getChunkN() == Integer.parseInt(header.get(3))) {
                Main.getBackup().getSending().get(i).setSent(true);
                break;
            }
        }

        if (Main.getDatabase().getFreeSpace() < body.length) {
            System.out.println("Not enough space for a new chunk.");
            return;
        }
        Boolean found = false;

        for (Chunk chunk : Main.getDatabase().getChunks()) {
            if (chunk.getFileId().equals(header.get(2))) {
                if (chunk.getChunkNo() == Integer.parseInt(header.get(3))) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            Chunk chunk;
            FileManager fm = new FileManager(header.get(2), Integer.parseInt(header.get(4)), true);
            fm.writeToFile(Integer.parseInt(header.get(3)), body);
            chunk = new Chunk(header.get(2), Integer.parseInt(header.get(3)), Integer.parseInt(header.get(4)));
            Main.getDatabase().addChunk(chunk);

        }

        String mssg = "STORED" + " " + Main.getVersion() + " " + header.get(2) + " " + header.get(3) + Main.getCRLF()
                .toString() + Main.getCRLF().toString();

        Random r = new Random();
        int time = r.nextInt(401);
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Main.getControl().send(mssg.getBytes(StandardCharsets.ISO_8859_1));
    }
}
