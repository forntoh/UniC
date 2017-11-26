package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static viewcontrollers.ClientTabController.*;

public class Connection {

    private String ipAddress, port;

    public Connection(String ipAddress, String port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public Socket makeConnection() throws IOException {
        Socket clientSocket = new Socket();
        int portNumber = Integer.parseInt(port);
        SocketAddress socketAddress = new InetSocketAddress(ipAddress, portNumber);
        //3s timeout
        clientSocket.connect(socketAddress, 3000);
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        return clientSocket;
    }
}