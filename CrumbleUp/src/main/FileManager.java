package main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

    private File file;
    public long fileSize;
    private InputStream in;
    private OutputStream out;
    private byte[] chunkData;
    private byte[] hashFileName;
    private String fileName;
    private Integer rep;
    private StringBuffer hashString;

    public FileManager(String newFileName, Integer newRep, Boolean load) {

        if (load) {
            hashString = new StringBuffer(newFileName);
            fileName = newFileName;
        } else {
            fileName = newFileName;
            encodeName();
        }
        rep = newRep;

    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.isFile();
        }
        return false;
    }

    public int getChunkSize(int chunkNo) {
        file = new File(Main.getDatabase().getUsername() + "/" + hashString.toString() + "/" + chunkNo + ".part");
        return (int) file.length();
    }

    public boolean deleteChunk(Integer chunkNo) {
        File chunk = new File(Main.getDatabase().getUsername() + "/" + hashString.toString() + "/" + chunkNo + ".part");
        if (chunk.exists()) {
            if (!chunk.delete()) {
                System.out.println("FAILED TO DELETE FILE!");
            }
            Main.getDatabase().removeChunk(hashString.toString(), chunkNo);
            return true;
        } else {
            System.out.println("Chunk doesn't exist!");
        }

        return false;
    }

    public boolean deleteFile() {

        int chunkNo = 0;

        while (true) {

            if (deleteChunk(chunkNo)) {
                chunkNo++;

            } else {
                break;
            }
        }

        File folder = new File(Main.getDatabase().getUsername() + "/" + hashString.toString());

        boolean result = folder.delete();

        if (result) {
            Main.getDatabase().removeFile(hashString.toString());
            return result;
        } else {
            return false;
        }

    }

    public boolean readChunk(Integer chunkNo, String username) {

        Main.getLogger().log("NAME: " + hashString.toString());
        File chunk = new File(username + "/" + hashString.toString() + "/" + chunkNo + ".part");

        if (chunk.exists()) {

            try {
                in = new BufferedInputStream(new FileInputStream(chunk));
            } catch (FileNotFoundException e) {
                System.err.println("Error creating input stream");
                e.printStackTrace();
            }

            chunkData = new byte[(int) chunk.length()];

            try {
                in.read(chunkData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    public synchronized void join(String username, StringBuffer password, boolean isDb) {
        Cipher cipher = null;
        int chunkNo = 0;

        if (!isDb) {


            try {
                cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }

        Main.getLogger().log("WHILE");
        while (readChunk(chunkNo, username)) {
            Main.getLogger().log("DECRYPT");
            if (!isDb) {
                chunkData = decryptBytes(chunkData, cipher, password);
            }
            Main.getLogger().log("WRITE");
            if (chunkNo == 0) {
                writeToFile(-1, chunkData, false, username, isDb);
            } else {
                writeToFile(-1, chunkData, true, username, isDb);
            }
            Main.getLogger().log("WRITTEN");
            if (!isDb) {
                deleteChunk(chunkNo);
            }
            chunkNo++;
        }
    }

    public synchronized boolean split(String username) {
        int totalBytesRead = 0;
        int bytesRead = 0;
        int chunkNo = 0;

        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        // Opens a file to split
        file = new File(fileName);

        fileSize = file.length();

        Integer chunksToBeCreated = (int) Math.ceil(fileSize / Main.getChunkSize());

        if (Main.getDatabase().getFreeSpace() >= chunksToBeCreated * Main.getChunkSize()) {

            try {
                in = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                System.err.println("Error creating input stream");
                e.printStackTrace();
            }

            while (totalBytesRead < fileSize) {

                if (fileSize - totalBytesRead < 64000) {
                    chunkData = new byte[(int) fileSize - totalBytesRead];
                } else {
                    chunkData = new byte[Main.getChunkSize()];
                }

                try {

                    bytesRead = in.read(chunkData, 0, chunkData.length);
                } catch (IOException e) {
                    System.err.println("Error reading stream");
                    e.printStackTrace();
                }

                chunkData = encryptBytes(chunkData, cipher);

                if (bytesRead >= 0) {
                    totalBytesRead += bytesRead;
                    Chunk chunk = new Chunk(hashString.toString(), chunkNo, rep);

                    writeToFile(chunkNo, chunkData, true, username, false);

                    Main.getDatabase().addChunk(chunk);

                    chunkNo++;
                } else {
                    System.err.println("Error reading file BytesRead: " + bytesRead);
                    break;
                }

            }

            if (fileSize % 64000 == 0) {
                File newFile = new File(Main.getDatabase().getUsername() + "/" + hashString.toString() + "/" +
                        chunkNo + ".part ");

                if (!newFile.exists()) {
                    try {
                        newFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Chunk chunk = new Chunk(hashString.toString(), chunkNo, rep);
                Main.getDatabase().addChunk(chunk);
            }

            if (rep != -1) {
                Main.getDatabase().addFile(hashString.toString(), fileName);
            }

        } else {
            System.err.println("Not enough space to backup file");
            return false;
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private synchronized static byte[] encryptBytes(byte[] chunkData, Cipher cipher) {
        Main.getLogger().log("Pass para o encrypt: " + new String(Main.getDatabase().getPassword()));
        byte[] password = Main.getDatabase().getPassword().toString().getBytes(StandardCharsets.ISO_8859_1);

        SecretKeySpec key = null;

        key = new SecretKeySpec(password, "DES");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key/*, new IvParameterSpec(Main.getDatabase().getUsernameByte())*/);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }/* catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }*/

        try {
            return cipher.doFinal(chunkData);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return chunkData;
    }

    private synchronized byte[] decryptBytes(byte[] chunkData, Cipher cipher, StringBuffer pass) {
        byte[] password = pass.toString().getBytes(StandardCharsets.ISO_8859_1);
        Main.getLogger().log("Pass para o decrypt: " + new String(password));

        SecretKeySpec key = null;
        key = new SecretKeySpec(password, "DES");

        try {
            Main.getLogger().log("Merdou no init da Cypher");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }/* catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }*/

        Main.getLogger().log("Merdou a finalizar");
        try {
            return cipher.doFinal(chunkData);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return chunkData;
    }

    public void writeToFile(int chunkNo, byte[] data, boolean isChunk, String username, boolean isDb) {
        File dir = new File(username + "/" + hashString.toString());

        if (!dir.exists()) {
            Boolean result = dir.mkdir();

            if (!result) {
                System.err.println("Error creating folder!");
            }
        }

        File newFile;

        if (chunkNo >= 0) {
            newFile = new File(username + "/" + hashString.toString() + "/" + chunkNo + "" +
                    ".part");
        } else {
            if (isDb) {
                Main.getLogger().log("NAME:" + hashString.toString());
                newFile = new File(username + "/database.cu");
            } else {
                newFile = new File(Main.getDatabase().getFile(hashString.toString()));
            }
        }

        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file!");
                e.printStackTrace();
            }
        }

        try {
            out = new BufferedOutputStream(new FileOutputStream(newFile, isChunk));
        } catch (FileNotFoundException e) {
            System.err.println("Error creating output stream");
            e.printStackTrace();
        }

        try {
            out.write(data);
        } catch (IOException e) {
            System.err.println("Error writing chunk to file");
            e.printStackTrace();
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encodeName() {

        File tmp = new File(fileName);

        String strTmp = fileName + tmp.lastModified();

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            assert digest != null;
            hashFileName = digest.digest(strTmp.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    /*
    Method to convert a byte array to hexadecimal form found on StackOverflow
     */
        hashString = new StringBuffer();
        for (byte aHashFileName : hashFileName) {
            hashString.append(Integer.toString((aHashFileName & 0xff) + 0x100, 16).substring(1));
        }

    }

    public static void writeDb(String path, int chunkNo, byte[] db, boolean restoring) {
        Main.getLogger().log("WRITEDB");
        File dir = null;
        if (restoring) {
            dir = new File(path);
        } else {
            dir = new File(Main.getDatabase().getUsername() + "/" + path);
        }

        Main.getLogger().log("FILE");

        if (!dir.exists()) {
            Boolean result = dir.mkdir();

            if (!result) {
                System.err.println("Error creating folder!");
            }
        }

        Main.getLogger().log("DIR");

        File newFile = null;
        if (restoring) {
            newFile = new File(path + "/" + chunkNo + ".part");
        } else {
            newFile = new File(Main.getDatabase().getUsername() + "/" + path + "/" + chunkNo + ".part");
        }

        Main.getLogger().log("NEWFILE");

        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file!");
                e.printStackTrace();
            }
        }

        Main.getLogger().log("OUT");

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(newFile, false));
        } catch (FileNotFoundException e) {
            System.err.println("Error creating output stream");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Main.getLogger().log("OUT WRITE");
        Main.getLogger().log("SIZE: " + db.length);

        try {
            out.write(db);
        } catch (IOException e) {
            System.err.println("Error writing chunk to file");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Main.getLogger().log("CLOSE");

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.getLogger().log("FINISH");

    }

    public boolean splitDb(String path, String id) {
        int totalBytesRead = 0;
        int bytesRead = 0;
        int chunkNo = 0;
        BufferedInputStream in = null;

        // Opens a file to split
        File file = new File(path);

        long fileSize = file.length();

        Integer chunksToBeCreated = (int) Math.ceil(fileSize / Main.getChunkSize());

        if (Main.getDatabase().getFreeSpace() >= chunksToBeCreated * Main.getChunkSize()) {

            try {
                in = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                System.err.println("Error creating input stream");
                e.printStackTrace();
            }

            while (totalBytesRead < fileSize) {

                byte[] chunkDb = null;
                if (fileSize - totalBytesRead < 64000) {
                    chunkDb = new byte[(int) fileSize - totalBytesRead];
                } else {
                    chunkDb = new byte[Main.getChunkSize()];
                }

                try {

                    bytesRead = in.read(chunkDb, 0, chunkDb.length);
                } catch (IOException e) {
                    System.err.println("Error reading stream");
                    e.printStackTrace();
                }

                if (bytesRead >= 0) {
                    totalBytesRead += bytesRead;

                    writeDb(id, chunkNo, chunkDb, false);

                    chunkNo++;
                } else {
                    System.err.println("Error reading file BytesRead: " + bytesRead);
                    break;
                }

            }

            if (fileSize % 64000 == 0) {
                File newFile = new File(Main.getDatabase().getUsername() + "/" + id + "/" +
                        chunkNo + ".part ");

                if (!newFile.exists()) {
                    try {
                        newFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            System.err.println("Not enough space to backup file");
            return false;
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public Integer getRep() {
        return rep;
    }

    public StringBuffer getHashString() {
        return hashString;
    }

}
