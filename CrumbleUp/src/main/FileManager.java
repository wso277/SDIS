package main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
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
        if (file.exists())
            return file.isFile();
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

    public boolean readChunk(Integer chunkNo) {

        File chunk = new File(Main.getDatabase().getUsername() + "/" + hashString.toString() + "/" + chunkNo + ".part");

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

    public synchronized void join() {
        int chunkNo = 0;

        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        while (readChunk(chunkNo)) {
            chunkData = decryptBytes(chunkData, cipher);
            if (chunkNo == 0) {
                writeToFile(-1, chunkData, false);
            } else {
                writeToFile(-1, chunkData, true);
            }
            deleteChunk(chunkNo);
            chunkNo++;
        }
    }

    public synchronized boolean split() {
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

                    writeToFile(chunkNo, chunkData, true);

                    Main.getDatabase().addChunk(chunk);

                    chunkNo++;
                } else {
                    System.err.println("Error reading file BytesRead: " + bytesRead);
                    break;
                }

            }

            if (fileSize % 64000 == 0) {
                File newFile = new File(hashString.toString() + "/" + chunkNo + ".part");

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

            Main.getDatabase().addFile(hashString.toString(), fileName);

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

    private synchronized byte[] encryptBytes(byte[] chunkData, Cipher cipher) {
        byte[] password = Main.getDatabase().getPassword();

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

    private synchronized byte[] decryptBytes(byte[] chunkData, Cipher cipher) {
        byte[] password = Main.getDatabase().getPassword();

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

    public void writeToFile(int chunkNo, byte[] data, boolean isChunk) {
        File dir = new File(Main.getDatabase().getUsername() + "/" + hashString.toString());

        if (!dir.exists()) {
            Boolean result = dir.mkdir();

            if (!result) {
                System.err.println("Error creating folder!");
            }
        }

        File newFile;

        if (chunkNo >= 0) {
            newFile = new File(Main.getDatabase().getUsername() + "/" + hashString.toString() + "/" + chunkNo + "" +
                    ".part");
        } else {
            newFile = new File(Main.getDatabase().getFile(hashString.toString()));
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
