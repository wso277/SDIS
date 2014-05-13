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

    public synchronized static void save() {

        ObjectOutputStream save = null;

        try {
            save = new ObjectOutputStream(new FileOutputStream("database.cu"));
        } catch (FileNotFoundException e) {
            System.err.println("Database.dbs not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error creating database.cu");
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
            load = new ObjectInputStream(new FileInputStream("database.cu"));
        } catch (FileNotFoundException e) {
            database = new Database();
            newdb = true;
        } catch (IOException e) {
            System.err.println("Error creating database.cu");
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
            save = new ObjectOutputStream(new FileOutputStream("network.cu"));
        } catch (FileNotFoundException e) {
            System.err.println("Network.cu not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error creating network.cu");
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

        try {
            load = new ObjectInputStream(new FileInputStream("network.cu"));
        } catch (FileNotFoundException e) {
            System.err.println("Network.cu not found!");
        } catch (IOException e) {
            System.err.println("Error creating network.cu");
            e.printStackTrace();
        }

        try {
            assert load != null;
            ipData.put("mc", (Address) load.readObject());
            ipData.put("mcr", (Address) load.readObject());
            ipData.put("mcb", (Address) load.readObject());

            System.out.println("\nRead network configurations!");
        } catch (ClassNotFoundException e) {
            System.err.println("network.cu not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading network configurations!");
            e.printStackTrace();
        }
    }

    /*
    Method to append two byte arrays found on StackOverflow
     */
    public static byte[] appendArray(byte[] arr, byte[] arr1) {
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

    public static String getCRLF() {
        String CRLF = "\r\n";
        return CRLF;
    }

    public static String getVersion() {
        return version;
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

    public static Database getDatabase() {
        return database;
    }

    public static ExecutorService getService() {
        return service;
    }

    public static Backup getBackup() {
        return backup;
    }

    public static Restore getRestore() {
        return restore;
    }

    public static Control getControl() {
        return control;
    }

    public static RestoreSend getRestoring() {
        return restoring;
    }

    public static void setRestoring(RestoreSend restoring) {
        Main.restoring = restoring;
    }
}