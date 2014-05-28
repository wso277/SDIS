package space_reclaim;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by João on 27/05/2014.
 */
public class ReclaimProcess {
    private String msg;
    private boolean running;
    ArrayList<FileChunks> filesToBeReclaimed;

    public ReclaimProcess() {
        msg = null;
        running = true;
    }

    public void run() {
        while(running) {
            /* Vai buscar o arraylist dos chunks que tenham chunks com rep degree menor que o desejado apos o space reclaim */
            filesToBeReclaimed = Main.getDatabase().getFilesToBeReclaimed();
            for (int i = 0; i < filesToBeReclaimed.size(); i++) {
                /* Pesquisa o hash map desse fileId a procura dos chunks com rep degree menor que o desejado */
                Iterator it = filesToBeReclaimed.get(i).getChunksRep().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    if ((Integer) pairs.getValue() < filesToBeReclaimed.get(i).getRepDegree()) {
                        /* Para cada um inicia um putchunk */
                        msg = "PUTCHUNK " + Main.getVersion() + " " + filesToBeReclaimed.get(i).getFileId()
                                + " " + pairs.getKey() + " " + filesToBeReclaimed.get(i).getRepDegree() +
                                Main.getCRLF() + Main.getCRLF();
                        Main.getBackup().send(msg.getBytes(StandardCharsets.ISO_8859_1));
                        it.remove();
                    }
                }
            }
        }
    }
}
