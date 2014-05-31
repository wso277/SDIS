package main;

import java.io.*;
import java.sql.Timestamp;

/**
 * Created by Vinnie on 30-May-14.
 */

public class Log {
    public static boolean PRINT_LOGS = false;
    public static boolean PRINT_TO_FILE = false;
    public static File logFile;
    public static PrintWriter logWriter;


    public Log(boolean printsToFile, boolean printsToConsole) {
        PRINT_LOGS = printsToConsole;
        if (printsToFile){
            PRINT_TO_FILE = true;
            File dir = new File("logs");

            if (!dir.exists()) {
                Boolean result = dir.mkdir();

                if (!result) {
                    System.err.println("Error creating log folder!");
                }
            }

            java.util.Date date = new java.util.Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String currentTimestamp = timestamp.toString();
            currentTimestamp = currentTimestamp.replace(':', '-');
            String fileName = "logs/" + currentTimestamp + ".log";
            logFile = new File(fileName);

            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file!");
                e.printStackTrace();
            }

            try {
                logWriter = new PrintWriter(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(String msg) {
        java.util.Date date = new java.util.Date();
        msg = msg.trim();
        String logMsg = new Timestamp(date.getTime()) + " => " + msg;

        if (PRINT_LOGS) {
            System.out.println(logMsg);
        }
        if(PRINT_TO_FILE) {
            logWriter.write(logMsg + "\n\n");
            logWriter.flush();
        }
    }

    public void close(){
        logWriter.close();
    }

    public void setPrinting(boolean state) {
        PRINT_LOGS = state;
    }
}