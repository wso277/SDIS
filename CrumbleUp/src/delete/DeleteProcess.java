package delete;

import main.Main;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by João on 22/05/2014.
 */
public class DeleteProcess extends Thread{
    private String msg;
    public DeleteProcess() {
        msg = null;
    }
    public void run() {
       Iterator it = Main.getDatabase().getDeletedFiles().entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            if((Integer)pairs.getValue() > 0){
                msg = "DELETE " + (String)pairs.getKey() + Main.getCRLF() + Main.getCRLF();
                Main.getControl().send(msg.getBytes(StandardCharsets.ISO_8859_1));
            }
        }
    }
}
