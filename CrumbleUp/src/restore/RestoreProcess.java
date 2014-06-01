package restore;

import communication.TCPCommunicator;
import main.Chunk;
import main.FileManager;
import main.Main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
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

        tmp = "";
        try {
            tmp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.getLogger().log("RECEIVE: " + tmp);

        String[] tmp1 = tmp.split("\\s+");

        for (String aTmp1 : tmp1) {
            header.add(aTmp1.trim());
        }

        if (header.get(0).equals("ME")) {
            restoreChunkProcess();
            Main.save(Main.getDatabase().getUsername());
        } else if (header.get(0).equals("DB")) {
            body = new byte[message.length - (tmp.length() + 4)];
            for (int i = tmp.length() + 4, j = 0; i < message.length; i++, j++) {
                body[j] = message[i];
            }
            restoreDBProcess();
        }
    }

    private void restoreDBProcess() {
        boolean found = false;
        Main.getLogger().log("PROCESS");
        try {

            for (Chunk ch : Main.getDatabase().getChunks()) {
                if (ch.getFileId().equals(header.get(1)) && ch.getChunkNo() == Integer.parseInt(header.get(2))) {

                    Main.getLogger().log("FOUND");
                    ch.setSent(true);
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            Main.getLogger().log("CATCH");
            FileManager.writeDb(Main.getRestoreDB().getUsername() + "/" + header.get(1),
                    Integer.parseInt(header.get(2)), body, true);
            found = true;
            Main.getLogger().log("WRITE");
            Main.getRestoreDB().setWaitingConfirmation(body.length);
            Main.getLogger().log("SAVED");
        }

        if (!found) {

            Main.getLogger().log("NOT FOUND");
            FileManager.writeDb(header.get(1), Integer.parseInt(header.get(2)), body, false);

        }
    }

    private void restoreChunkProcess() {

        Chunk chunk;
        Boolean found = false;
        Boolean repFound = false;
        Integer repDegree = 2;

        if (Main.getRestoring().getFileId().equals(header.get(1))) {

            TCPCommunicator receiver;
            try {
                try {
                    receiver = new TCPCommunicator(header.get(3), Integer.parseInt(header.get(4)), false);
                } catch (SocketTimeoutException e) {
                    String get = "GETCHUNK " + Main.getVersion() + " " + header.get(1) +
                            " " + header.get(2) + Main.getCRLF() + Main.getCRLF();
                    Main.getControl().send(get.getBytes(StandardCharsets.ISO_8859_1));
                    return;
                }

                byte[] msg = null;
                msg = receiver.receive();

                receiver.close();

                String tmp;
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg)));

                tmp = "";
                try {
                    tmp = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Main.getLogger().log("Sent: " + tmp);

                String[] tmp1 = tmp.split("\\s+");
                ArrayList<String> chunkMsg = new ArrayList<>();
                for (String aTmp1 : tmp1) {
                    chunkMsg.add(aTmp1.trim());
                }

                body = new byte[msg.length - (tmp.length() + 4)];
                for (int i = tmp.length() + 4, j = 0; i < msg.length; i++, j++) {
                    body[j] = msg[i];
                }

                FileManager fm = new FileManager(chunkMsg.get(1), repDegree, true);
                fm.writeToFile(Integer.parseInt(chunkMsg.get(2)), body, true, Main.getDatabase().getUsername(), false);
                chunk = new Chunk(chunkMsg.get(1), Integer.parseInt(chunkMsg.get(2)), repDegree);
                Main.getDatabase().addChunk(chunk);
                Main.getRestoring().setWaitingConfirmation(body.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
