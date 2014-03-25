package restore;

public class RestoreProcessThread extends Thread {

    private String fileid;
    private String chunkNo;

    public RestoreProcessThread(String newid, String newchunkNo) {

        fileid = newid;
        chunkNo = newchunkNo;

        run();
    }

    @Override
    public void run() {

    }
}
