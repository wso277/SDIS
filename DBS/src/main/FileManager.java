package main;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

    private File file;
    private long fileSize;
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

    public int getChunkSize(int chunkNo) {
        file = new File(hashString.toString() + "/" + chunkNo + ".part");
        return (int)file.length();
    }

    public boolean deleteChunk(Integer chunkNo) {

        File chunk = new File(hashString.toString() + "/" + chunkNo + ".part");
        if (chunk.exists()) {
            if(!chunk.delete()) {
                System.out.println("FAILED TO DELETE FILE!");
            }
            Main.getDatabase().removeChunk(hashString.toString(), chunkNo);
            return true;
        }


        return false;
    }

    public void delete() {

        int chunkNo = 0;

        while (true) {

            if (deleteChunk(chunkNo)) {
                chunkNo++;

            } else {
                break;
            }
        }

        File folder = new File(hashString.toString());

        folder.delete();

        Main.getDatabase().removeFile(hashString.toString());
    }

    public boolean readChunk(Integer chunkNo) {

        File chunk = new File(hashString.toString() + "/" + chunkNo + ".part");

        if (chunk.exists()) {

            try {
                in = new BufferedInputStream(new FileInputStream(chunk));
            } catch (FileNotFoundException e) {
                System.err.println("Error creating input stream");
                e.printStackTrace();
            }

            chunkData = new byte[(int)chunk.length()];

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

    public void join() {
        int chunkNo = 0;

        while (readChunk(chunkNo)) {
            //TODO FOI MUDADO PARA -1 POR ORDEM DO MENINO WILSON
            writeToFile(-1, chunkData);

            chunkNo++;
        }
    }

    public boolean split() {
        int totalBytesRead = 0;
        int bytesRead = 0;
        int chunkNo = 0;

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

                chunkData = new byte[Main.getChunkSize()];

                try {
                    bytesRead = in.read(chunkData, 0, Main.getChunkSize());
                } catch (IOException e) {
                    System.err.println("Error reading stream");
                    e.printStackTrace();
                }

                if (bytesRead >= 0) {
                    totalBytesRead += bytesRead;
                    Chunk chunk = new Chunk(hashString.toString(), chunkNo, rep);

                    writeToFile(chunkNo, chunkData);

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

    public void writeToFile(int chunkNo, byte[] data) {
        File dir = new File(hashString.toString());

        if (!dir.exists()) {
            Boolean result = dir.mkdir();

            if (!result) {
                System.err.println("Error creating folder!");
            }
        }

        File newFile;

        if (chunkNo >= 0) {
            newFile = new File(hashString.toString() + "/" + chunkNo + ".part");
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
            out = new BufferedOutputStream(new FileOutputStream(newFile, true));
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

        hashString = new StringBuffer();
        for (byte aHashFileName : hashFileName) {
            hashString.append(Integer.toString((aHashFileName & 0xff) + 0x100, 16).substring(1));
        }

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public void setChunkData(byte[] chunkData) {
        this.chunkData = chunkData;
    }

    public byte[] getHashFileName() {
        return hashFileName;
    }

    public void setHashFileName(byte[] hashFileName) {
        this.hashFileName = hashFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getRep() {
        return rep;
    }

    public void setRep(Integer rep) {
        this.rep = rep;
    }

    public StringBuffer getHashString() {
        return hashString;
    }

    public void setHashString(StringBuffer hashString) {
        this.hashString = hashString;
    }
}
