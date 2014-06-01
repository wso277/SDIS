package control;

import backup.BackupSend;
import communication.TCPCommunicator;
import main.Chunk;
import main.FileManager;
import main.Main;
import space_reclaim.FileChunks;
import space_reclaim.ReclaimProcess;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

        Main.getLogger().log("RECEIVED: " + tmp);

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
            Main.save(Main.getDatabase().getUsername());

        } else if (header.get(0).equals("REMOVED")) {
            if (Main.getVersion().equals(header.get(1))) {
                removedProcess();
                Main.save(Main.getDatabase().getUsername());
            }
        } else if (header.get(0).equals("STORED")) {
            if (Main.getVersion().equals(header.get(1))) {
                storedProcess();
                Main.save(Main.getDatabase().getUsername());
            }
        } else if (header.get(0).equals("DELETED")) {
            updateDeletes();
            Main.save(Main.getDatabase().getUsername());
        } else if (header.get(0).equals("GETDB")) {
            processDB();
        } else if (header.get(0).equals("IN")) {
            processIn();
        } else {
            System.err.println("Operation Invalid!");
        }

    }

    private void processIn() {
        Main.getService().submit(new ReclaimProcess());
    }

    private void processDB() {
        FileManager fm = new FileManager(header.get(1), 0, true);
        if (fm.readChunk(Integer.parseInt(header.get(2)), Main.getDatabase().getUsername())) {
            Chunk ch = new Chunk(header.get(1), Integer.parseInt(header.get(2)), 0);

            Main.getDatabase().addChunk(ch);
            String msg = "DB " + header.get(1) + " " + header.get(2) + Main.getCRLF() + Main.getCRLF();

            byte[] msgByte = msg.getBytes(StandardCharsets.ISO_8859_1);
            byte[] msgFull = Main.appendArray(msgByte, fm.getChunkData());

            Random r = new Random();
            try {
                sleep(r.nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!ch.getSent()) {
                Main.getRestore().send(msgFull);
            }

            Main.getDatabase().removeChunk(header.get(1), Integer.parseInt(header.get(2)));
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
            chunk.readChunk(Integer.parseInt(header.get(3)), Main.getDatabase().getUsername());

            String ip = "";
            try {
                ip = InetAddress.getLocalHost().toString().split("/")[1];
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            int tcp_port = Main.getTCPport();
            String message = "ME " + header.get(2) + " " + header.get(3) + " " + ip + " " + tcp_port + Main.getCRLF() +
                    Main.getCRLF();

            byte[] msg = message.getBytes(StandardCharsets.ISO_8859_1);
            Main.getRestore().send(msg);
            Main.getLogger().log("Received: " + message);

            try {
                TCPCommunicator tcpSocket = new TCPCommunicator(ip, tcp_port, true);
                String chunkMsg = "CHUNK " + header.get(2) + " " + header.get(3) +
                        Main.getCRLF() + Main.getCRLF();

                Main.getLogger().log("Sent: " + chunkMsg);

                byte[] chunkBytes = Main.appendArray(chunkMsg.getBytes(StandardCharsets.ISO_8859_1),
                        chunk.getChunkData());
                tcpSocket.send(chunkBytes);

                tcpSocket.close();

            } catch (SocketTimeoutException e) {
                System.out.println("I was not the chosen one!");
            } catch (IOException e) {
                e.printStackTrace();
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
                    BackupSend send = new BackupSend(chunk.getFileId(), chunk.getRepDegree(), false,
                            chunk.getChunkNo(), chunk.getKnownReps());
                    Main.getBackup().addSending(send);
                    Main.getService().submit(send);
                }
                break;
            }

        }

    }

    private void deleteProcess() {
        FileManager del = new FileManager(header.get(1), 0, true);
        boolean result = del.deleteFile();

        for (FileChunks file : Main.getDatabase().getFilesToBeReclaimed()) {
            if (file.getFileId().equals(header.get(1))) {
                Main.getDatabase().getFilesToBeReclaimed().remove(file);
                break;
            }
        }

        if (result) {
            String msg = "DELETED " + header.get(1) + Main.getCRLF() + Main.getCRLF();
            Main.getLogger().log("Sent: " + msg);
            Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));

        }
    }

    private void updateDeletes() {
        Integer reps = Main.getDatabase().getDeletedFiles().get(header.get(1));

        if (reps != null) {
            Main.getDatabase().changeRepDegree(header.get(1), reps - 1);
        }

        Main.getService().submit(new ReclaimProcess());
    }

}
