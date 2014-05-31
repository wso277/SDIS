package cli;

import backup.BackupSend;
import communication.Address;
import communication.Network;
import delete.Delete;
import main.Database;
import main.Main;
import restore.RestoreSend;
import space_reclaim.SpaceReclaim;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cli {

    private final BufferedReader in;
    private String input;
    private static String username;
    private static String password;

    public Cli() {

        in = new BufferedReader(new InputStreamReader(System.in));

        username = "";

        password = "";

        cliLogin();

        Main.loadNetwork();

        introNetwork();
    }

    private void introNetwork() {
        input = "";
        while (!input.equals("1") && !input.equals("2")) {

            System.out.print("Choose a command:\n" + "1. Create new network configuration\n" + "2. Load existing " +
                    "network configuration\n\n" + "Option: ");
            System.out.flush();

            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clearConsole();
        }
        processIntroNetwork();
    }

    private void processIntroNetwork() {
        switch(input) {
            case "1":
                createNetworkConfiguration();
                break;
            case "2":
                loadNetworkConfiguration();
                break;
            default:
                System.out.println("Invalid Input!");
                break;
        }
    }

    private void createNetworkConfiguration() {
        System.out.println("Enter the configuration name: ");
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        processFirstMenu(input);
    }

    private void loadNetworkConfiguration() {
        HashMap<String, Network> confs = Main.getConfigurations();
        int i = 0, size = confs.size(), op = -1;
        ArrayList<String> tmp = new ArrayList<>();
        while (op < 1 || op > size) {
            System.out.println("Available configurations: ");
            Iterator it = confs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                System.out.println(i + 1 + " - " + pairs.getKey());
                i++;
                tmp.add((String) pairs.getKey());
            }

            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            op = Integer.parseInt(input);
        }
        Main.chooseNetwork(tmp.get(op-1));
    }

    /*private void chooseNetworkConfigType() {
        input = "";
        while (!input.equals("1") && !input.equals("2")) {

            System.out.print("Choose a command:\n" + "1. Insert new network configurations\n" + "2. Load existing " +
                    "network configurations\n\n" + "Option: ");
            System.out.flush();

            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clearConsole();
        }

        processFirstMenu();
    }*/

    private String readIp(String module) throws IOException {
        System.out.print("Insert " + module + " IP: ");
        String result;
        boolean failed = false;
        do {
            if (failed) {
                System.out.println("Invalid Multicast Address! Try again: ");
            }
            result = in.readLine();
            failed = true;
        } while (!result.matches("2(?:2[4-9]|3\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d?|0)){3}"));
        return result;
    }

    private int readPort(String module) throws IOException {
        System.out.print("Insert " + module + " Port: ");
        int result;
        boolean failed = false;
        do {
            if (failed) {
                System.out.println("Invalid Port! Try again: ");
            }
            result = Integer.parseInt(in.readLine());
            failed = true;
        } while (result < 1 || result > 65535);
        return result;
    }

    private void processFirstMenu(String configurationName) {
            Address mc = null;
            Address mcr = null;
            Address mcb = null;
            try {
                mc = new Address(readIp("Control"), readPort("Control"));
                mcr = new Address(readIp("Restore"), readPort("Restore"));
                mcb = new Address(readIp("Backup"), readPort("Backup"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        Main.saveNetwork(configurationName,mc, mcr, mcb);

        Main.loadNetwork();
        Main.chooseNetwork(configurationName);
    }

    public void menu() {
        input = "";
        clearConsole();

        while (!input.equals("exit") && !input.equals("Exit") && !input.equals("6")) {

            System.out.print("\nChoose a command:\n" + "1. Backup a File\n" + "2. Restore a File\n" + "3. Delete a " +
                    "File\n" + "4. Reclaim disk space\n" + "5. Increase disk size\n" + "6. Exit\n\n" + "Option: ");
            System.out.flush();
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clearConsole();
            processInput();
        }
    }

    private void cliLogin() {
        do {
            System.out.print("\nChoose a command:\n" + "1. Create Account\n" + "2. Log in\n" + "3. Exit\n\n" +
                    "Option: ");
            System.out.flush();
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!input.equals("1") && !input.equals("2") && !input.equals("3"));

        processIntroMenu();
    }

    private void processIntroMenu() {
        switch (input) {
            case "1":
                processRegister();
                break;
            case "2":
                processLogin();
                break;
            case "3":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid Option!\n");
        }
    }

    public static void processRegister() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your desired username: ");
        try {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Enter your desired password: ");
        try {
            password = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.setDatabase(new Database(username, password));
        File dir = new File(username);

        if (!dir.exists()) {
            dir.mkdir();
        }
        Main.save(username);
    }

    private void processLogin() {
        Boolean res = false;
        while (!res) {
            System.out.println("Enter your username: ");
            try {
                username = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            res = Main.load(username);

            if (res) {
                res = false;
                while (!res) {
                    System.out.println("Enter your password: ");
                    try {
                        password = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Main.getDatabase().login(password)) {
                        res = true;
                    } else {
                        res = false;
                        System.out.println("Wrong Password!");
                    }
                }
            } else {
                System.out.println("Wrong Username!");
            }


        }

        Main.save(username);
    }

    private void processInput() {

        switch (input) {
            case "1":
            case "backup":
            case "Backup":
            case "BACKUP":
                processBackupInput();
                break;
            case "2":
            case "restore":
            case "Restore":
            case "RESTORE":
                processRestoreInput();
                break;
            case "3":
            case "delete":
            case "Delete":
            case "DELETE":
                processDeleteInput();
                break;
            case "4":
            case "reclaim":
            case "Reclaim":
            case "RECLAIM":
                processReclaimInput();
                break;
            case "5":
            case "increase":
            case "Increase":
            case "INCREASE":
                processIncreaseInput();
                break;
            case "6":
            case "exit":
            case "Exit":
            case "EXIT":
                processExitInput();
                break;
            default:
                System.out.println("Invalid Option!\n");
        }
    }

    private void processExitInput() {
        System.out.println("Exiting program!");
        Main.getBackup().setRunning(false);
        Main.getControl().setRunning(false);
        Main.getRestore().setRunning(false);
        Main.getDelete().setRunning(false);
        Main.getBackup().close();
        Main.getControl().close();
        Main.getRestore().close();
        Main.getLogger().close();
    }

    private void processIncreaseInput() {
        System.out.println("Space currently occupied - " + Database.getDiskSize());
        System.out.print("Choose space to add (byte): ");
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(input) > 0) {
            Database.setDiskSize(Database.getDiskSize() + Integer.parseInt(input));
        } else {
            System.out.println("Invalid Size. Choose from the available range!");
        }
    }

    private void processReclaimInput() {
        System.out.println("Space currently occupied - " + Database.getDiskSize());
        System.out.print("Choose space to release (1 byte to " + (Database.getDiskSize() - 64) + " bytes): ");
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(input) > 0 && Integer.parseInt(input) <= (Database.getDiskSize() - 64)) {
            new SpaceReclaim(Integer.parseInt(input)).process();
        } else {
            System.out.println("Invalid Size. Choose from the available range!");
        }
    }

    private void processDeleteInput() {
        String result;
        Main.getDatabase().showBackedUpFiles();
        System.out.print("Choose file to be deleted (number): ");
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = Main.getDatabase().getHash(Integer.parseInt(input));
        if (!result.equals("fail")) {
            System.out.println("Sending DELETE message!");
            new Delete(result).process();
        } else {
            System.out.println("Invalid file. Choose one of the availvable numbers!");
        }
    }

    private void processRestoreInput() {
        String result;
        Main.getDatabase().showBackedUpFiles();
        System.out.print("Choose file to be backed up (number): ");
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = Main.getDatabase().getHash(Integer.parseInt(input));
        if (!result.equals("fail")) {
            System.out.println("Sending RESTORE message!");
            Main.setRestoring(new RestoreSend(result));
            Main.getRestoring().process();
        } else {
            System.out.println("Invalid file. Choose one of the availvable numbers!");
        }
    }

    private void processBackupInput() {
        System.out.print("Type path to file: ");
        String filePath = "";
        try {
            filePath = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Main.fileExists(filePath)) {
            System.out.println("File Exists! Enter replication degree: ");
            String repDegree = "";
            try {
                repDegree = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Integer.parseInt(repDegree) >= 1) {
                BackupSend send = new BackupSend(filePath, Integer.parseInt(repDegree), true, -1, -1);
                Main.getBackup().addSending(send);
                System.out.println("Iniatilizing backup! Message will be outputted when finished.");
                Main.getService().submit(send);
            }
        } else {
            System.out.println("Invalid file path. Please try again with a valid file path");
        }
    }

    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception exception) {
            // Handle exception.
        }
    }
}