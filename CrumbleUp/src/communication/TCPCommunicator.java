package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public TCPCommunicator(String newIp, Integer newPort, Boolean isServer) throws SocketTimeoutException, IOException {

        this.isServer = isServer;
        port = newPort;

        if (isServer) {
            server = new ServerSocket(port);
            server.setSoTimeout(5000);
            socket = server.accept();
        } else {

            address = InetAddress.getByName(newIp);
            socket = new Socket(address, port);

        }
    }

    public byte[] receive() throws SocketException, SocketTimeoutException {
        DataInputStream in;
        byte[] msg = null;

        socket.setSoTimeout(5000);
        try {
            in = new DataInputStream(socket.getInputStream());

            int msgSize = in.readInt();
            System.out.println("RECEIVE SIZE = " + msgSize);
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

  /*  public static void main(String args[])
    {
        TCPCommunicator communicator;
        Scanner s = new Scanner(System.in);
        System.out.println("Server? ");
        int server = s.nextInt();
        if(server == 1)
        {
            communicator = new TCPCommunicator("localhost", 1234, true);
            System.out.println("UNLOCKED");
            byte []answer = communicator.receive();
            System.out.println(new String(answer,StandardCharsets.UTF_8));
            String sendBack = "Received";
            communicator.send(sendBack.getBytes(StandardCharsets.UTF_8));
        }

        else
        {
            communicator = new TCPCommunicator("localhost", 1234, false);
            System.out.println("UNLOCKED");
            String msg = "Test";
            communicator.send(msg.getBytes(StandardCharsets.UTF_8));
            byte[]answer = communicator.receive();
            System.out.println(new String(answer,StandardCharsets.UTF_8));
        }
    }*/

}
