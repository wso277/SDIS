package main;

import backup.Backup;
import cli.Cli;
import communication.Address;
import control.Control;
import restore.Restore;
import restore.RestoreSend;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String CRLF = "\r\n";
    private static String version = "1.0";
    private static int diskSize = 100000000;
    private static int chunkSize = 64000;
    private static Backup backup;
    private static Restore restore;
    private static Control control;
    private static Cli cli;
    private static Database database;
    private static HashMap<String, Address> ipData;
    private static ExecutorService service;
    private static RestoreSend restoring;


    public static void main(String[] args) throws IOException {

        // Load database
        load();

        /*FileManager split = new FileManager("/home/wso277/Desktop/image1.jpg", 0, false);
        split.split();
        FileManager split1 = new FileManager("/home/wso277/Desktop/image2.jpg", 0, false);
        split1.split();
        FileManager split2 = new FileManager("/home/wso277/Desktop/image3.jpg", 0, false);
        split2.split();
        C:/Users/Vinnie/Desktop/son.pdf
        */

        // Initializing job queue
        service = Executors.newFixedThreadPool(12);

        // Store address info
        ipData = new HashMap<>();

        // Initializing components
        cli = new Cli();
        backup = new Backup(ipData.get("mcb").getIp(), ipData.get("mcb").getPort());
        control = new Control(ipData.get("mc").getIp(), ipData.get("mc").getPort());
        restore = new Restore(ipData.get("mcr").getIp(), ipData.get("mcr").getPort());

        // Pushing main components to job queue
        service.submit(backup);
        service.submit(control);
        service.submit(restore);
        cli.menu();

        // When cli ends
        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // save database
        save();

    }

    public static void shutdown() {
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        save();
        return;
    }

    public synchronized static void save() {

        ObjectOutputStream save = null;

        try {
            save = new ObjectOutputStream(new FileOutputStream("database.dbs"));
        } catch (FileNotFoundException e) {
            System.err.println("Database.dbs not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error creating database.dbs");
            e.printStackTrace();
        }

        try {
            assert save != null;
            save.writeObject(database);
        } catch (IOException e) {
            System.err.println("Error saving database");
            e.printStackTrace();
        }
    }

    private static void load() {

        ObjectInputStream load = null;
        Boolean newdb = false;

        try {
            load = new ObjectInputStream(new FileInputStream("database.dbs"));
        } catch (FileNotFoundException e) {
            database = new Database();
            newdb = true;
        } catch (IOException e) {
            System.err.println("Error creating database.dbs");
            e.printStackTrace();
        }

        if (!newdb) {

            try {
                assert load != null;
                database = (Database) load.readObject();
                System.out.println("Read database!");
            } catch (ClassNotFoundException e) {
                System.err.println("Database not found!");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Error loading database!");
                e.printStackTrace();
            }
        }
    }

    public static void saveNetwork(Address mc, Address mcr, Address mcb) {

        ObjectOutputStream save = null;

        try {
            save = new ObjectOutputStream(new FileOutputStream("network.dbs"));
        } catch (FileNotFoundException e) {
            System.err.println("Network.dbs not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error creating network.dbs");
            e.printStackTrace();
        }

        try {
            assert save != null;
            save.writeObject(mc);
            save.writeObject(mcr);
            save.writeObject(mcb);
        } catch (IOException e) {
            System.err.println("Error saving network configurations");
            e.printStackTrace();
        }
    }

    public static void loadNetwork() {

        ObjectInputStream load = null;
        Boolean newdb = false;

        try {
            load = new ObjectInputStream(new FileInputStream("network.dbs"));
        } catch (FileNotFoundException e) {
            System.err.println("Network.dbs not found!");
        } catch (IOException e) {
            System.err.println("Error creating network.dbs");
            e.printStackTrace();
        }

        if (!newdb) {

            try {
                assert load != null;
                ipData.put("mc", (Address) load.readObject());
                ipData.put("mcr", (Address) load.readObject());
                ipData.put("mcb", (Address) load.readObject());
                System.out.println("\nRead network configurations!");
            } catch (ClassNotFoundException e) {
                System.err.println("network.dbs not found!");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Error loading network configurations!");
                e.printStackTrace();
            }
        }
    }

    public static byte[] appendArray( byte[] arr, byte[] arr1 ) {
        int aLen = arr.length;
        int bLen = arr1.length;
        byte[] C = new byte[aLen + bLen];
        System.arraycopy(arr, 0, C, 0, aLen);
        System.arraycopy(arr1, 0, C, aLen, bLen);
        return C;
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean checkVersion(String ver) {
        return version.equals(ver);
    }

    public static Address getipData(String channel) {
        return ipData.get(channel);
    }

    public static String getCRLF() {
        return CRLF;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        Main.version = version;
    }

    public static int getDiskSize() {
        return diskSize;
    }

    public static void setDiskSize(int diskSize) {
        Main.diskSize = diskSize;
    }

    public static int getChunkSize() {
        return chunkSize;
    }

    public static void setChunkSize(int chunkSize) {
        Main.chunkSize = chunkSize;
    }

    public static HashMap<String, Address> getIpData() {
        return ipData;
    }

    public static void setIpData(HashMap<String, Address> ipData) {
        Main.ipData = ipData;
    }

    public static Database getDatabase() {
        return database;
    }

    public static void setDatabase(Database database) {
        Main.database = database;
    }

    public static ExecutorService getService() {
        return service;
    }

    public static Backup getBackup() {
        return backup;
    }

    public static void setBackup(Backup backup) {
        Main.backup = backup;
    }

    public static Restore getRestore() {
        return restore;
    }

    public static void setRestore(Restore restore) {
        Main.restore = restore;
    }

    public static Control getControl() {
        return control;
    }

    public static void setControl(Control control) {
        Main.control = control;
    }

    public static Cli getCli() {
        return cli;
    }

    public static void setCli(Cli cli) {
        Main.cli = cli;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public static void setService(ExecutorService service) {
        Main.service = service;
    }

    public static RestoreSend getRestoring() {
        return restoring;
    }

    public static void setRestoring(RestoreSend restoring) {
        Main.restoring = restoring;
    }
}
