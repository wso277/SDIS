package communication;

import java.io.*;
import java.net.*;

/**
 * Created by wso277 on 5/13/14.
 */
public class TCPCommunicator {

    private final Integer port;
    private Socket socket;
    private ServerSocket server;
    private InetAddress address;
    private Boolean isServer;

    public TCPCommunicator(String newIp, Integer newPort, Boolean isServer) {

        this.isServer = isServer;
        port = newPort;

        if (isServer) {

            try {
                server = new ServerSocket(port);
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                address = InetAddress.getByName(newIp);
                socket = new Socket(address, port);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] receive() {
        DataInputStream in;
        byte[] msg = null;
        try {
            in = new DataInputStream(socket.getInputStream());

            int msgSize = in.readInt();
            msg = new byte[msgSize];
            in.readFully(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return msg;
    }

    public void send(byte[] msg) {
        DataOutputStream out;

        try {
            out = new DataOutputStream(socket.getOutputStream());
            int msgSize = msg.length;
            out.writeInt(msgSize);
            out.write(msg, 0, msgSize);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (isServer) {
                server.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
