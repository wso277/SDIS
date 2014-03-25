package restore;

import java.util.ArrayList;

public class RestoreProcessThread extends Thread {

    public String message;
    public ArrayList<String> header;

    public RestoreProcessThread(String newmessage) {
        message = newmessage;
    }

    public void run() {
        header = new ArrayList<String>();

        String[] tmp = message.split("\\s+");

        for (int i = 0; i < tmp.length; i++) {
            header.add(tmp[i].trim());
        }
    }
}
