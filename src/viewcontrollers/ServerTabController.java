package viewcontrollers;

import ipaddress.GetFreePort;
import ipaddress.GetMyIpAddress;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import network.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Utils.showDialogMessage;

public class ServerTabController {

    @FXML
    private Button btnResetServer;
    @FXML
    private Button btnStartServer;
    @FXML
    private Label labelStatus;
    @FXML
    private Text txtFieldIP;
    @FXML
    private Text txtFieldPort;

    public static ServerSocket serverSocket = null;
    public static Socket clientSocket = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;
    public static ObjectOutputStream objectOutputStream = null;
    public static ObjectInputStream objectInputStream = null;

    public void onClickStartServer(MouseEvent mouseEvent) {
        setConnectionDetails();
        btnResetServer.setDisable(false);
        btnStartServer.setDisable(true);
    }

    public void onClickResetServer(MouseEvent mouseEvent) {
        btnResetServer.setDisable(true);
        btnStartServer.setDisable(false);
        labelStatus.setText("Server stopped");
        connectionClosed();
    }

    private void setConnectionDetails() {
        String ipAddresses[] = new GetMyIpAddress().ipAddress();
        int port = new GetFreePort().getFreePort();
        String ipAddress = ipAddresses[0];
        if (ipAddresses[1] != null)
            ipAddress = ipAddress + " | " + ipAddresses[1];

        txtFieldIP.setText(ipAddress);
        txtFieldPort.setText(Integer.toString(port));
        labelStatus.setText("Not Connected");

        if (ipAddresses[0].equals("127.0.0.1")) {
            btnResetServer.setDisable(true);
            btnStartServer.setDisable(false);
            showDialogMessage("Connection your PC to Android phone hotspot or connect both devices to a local network.");
        } else {
            try {
                serverSocket = new ServerSocket(port);
                new Thread(() -> new Server().connect(btnResetServer, labelStatus)).start();
            } catch (Exception e) {
                showDialogMessage("Error in initializing server");
                e.printStackTrace();
            }
        }
    }

    //this method is called from fragments to send message to client (Local)
    public static void sendMessageToClient(Object message) throws IOException {
        if (clientSocket == null) return;
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    public static void connectionClosed() {
        try {
            objectInputStream.close();
            clientSocket.close();
            serverSocket.close();
            inputStream.close();
            outputStream.close();
            objectOutputStream.close();
        } catch (IOException | NullPointerException ignored) {}
    }
}
