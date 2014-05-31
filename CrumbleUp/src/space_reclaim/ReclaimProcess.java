package space_reclaim;

import backup.BackupSend;
import main.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by João on 27/05/2014.
 */
public class ReclaimProcess extends Thread {

    public void run() {
        /* Vai buscar o arraylist dos chunks que tenham chunks com rep degree menor que o desejado apos o space
        reclaim */
        /* Vai tu */
        ArrayList<FileChunks> filesToBeReclaimed = Main.getDatabase().getFilesToBeReclaimed();

        for (FileChunks file : filesToBeReclaimed) {
            /* Pesquisa o hash map desse fileId a procura dos chunks com rep degree menor que o desejado */
            Iterator it = file.getChunksRep().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                if ((Integer) pairs.getValue() < file.getRepDegree()) {
                    /* Para cada um inicia um putchunk */
                    BackupSend send = new BackupSend(file.getFileId(), file.getRepDegree(), false,
                            (Integer) pairs.getKey(), (Integer) pairs.getValue());
                    Main.getBackup().addSending(send);
                    Main.getService().submit(send);
                }
            }
        }
    }
}
