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
                fm.writeToFile(Integer.parseInt(chunkMsg.get(2)), body, true);
                chunk = new Chunk(chunkMsg.get(1), Integer.parseInt(chunkMsg.get(2)), repDegree);
                Main.getDatabase().addChunk(chunk);
                Main.getRestoring().setWaitingConfirmation(body.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
