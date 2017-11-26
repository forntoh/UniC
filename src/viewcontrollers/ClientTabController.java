package viewcontrollers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import network.Screen;
import network.client.Connection;
import network.client.handlers.KeyboardEventHandler;
import network.client.handlers.MouseEventHandler;
import network.client.interfaces.OnChangeScreenListener;
import network.client.interfaces.OnMouseMoveListener;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Utils.showDialogMessage;
import static utils.Validate.validateIP;
import static utils.Validate.validatePort;

/**
 * Created by Forntoh on 25-Nov-17.
 */
public class ClientTabController {

    @FXML
    private TextField ipAddrField;
    @FXML
    private TextField portField;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelScreenSize;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnControl;

    private static Socket clientSocket = null;
    public static ObjectOutputStream objectOutputStream = null;
    public static ObjectInputStream objectInputStream = null;
    private static Screen remoteScreen;
    private static Screen localScreen;
    private MouseEventHandler mouseMotionHandler;
    private KeyboardEventHandler keyboardEventHandler;
    private Robot robot;
    private Stage primaryStage;
    private static final int SHIFT_DIST = 5;

    public void onClickBtnConnect(MouseEvent mouseEvent) {
        String preStatus = labelStatus.getText();
        String ip = ipAddrField.getText();
        String port = portField.getText();

        if (!validateIP(ip) || !validatePort(port))
            delayChangeStatus("Invalid IP Address or port!!!", preStatus);
        else {
            Connection connection = new Connection(ip, port);
            try {
                clientSocket = connection.makeConnection();
                labelStatus.setText("Connected");
                btnConnect.setDisable(true);
                btnDisconnect.setDisable(false);
                btnControl.setDisable(false);
                new Thread(this::receiveInfoFromServer).start();
            } catch (IOException e) {
                labelStatus.setText("Not connected");
                showDialogMessage("Error while connecting to server");
                e.printStackTrace();
            }
        }
    }

    public void onClickBtnDisconnect(MouseEvent mouseEvent) {
        btnConnect.setDisable(false);
        btnDisconnect.setDisable(true);
        btnControl.setDisable(true);
        Platform.runLater(() -> labelStatus.setText("Not connected"));
        Platform.runLater(() -> labelScreenSize.setText("N/A"));
        connectionClosed();
    }

    public void onClickBtnControl(MouseEvent mouseEvent) {
        if (primaryStage == null) primaryStage = initializeOverlayStage();
        primaryStage.show();
        try {
            robot = new Robot();
            localScreen = new Screen();
            localScreen.setScreenPosition("LEFT");
        } catch (AWTException e) {
            e.printStackTrace();
        }

        keyboardEventHandler = new KeyboardEventHandler();
        mouseMotionHandler = new MouseEventHandler(localScreen, remoteScreen);

        mouseMotionHandler.setOnMouseMoveListener(onMouseMoveListener);
        mouseMotionHandler.setOnScreenChangeListener(changeScreenListener);

        primaryStage.addEventFilter(MouseEvent.ANY, mouseMotionHandler);
        primaryStage.addEventFilter(KeyEvent.ANY, keyboardEventHandler);
    }

    private void receiveInfoFromServer() {
        try {
            while (true) {
                String message = (String) objectInputStream.readObject();
                if (message != null) {
                    switch (message) {
                        case "SCREEN_DIMENSIONS":
                            int width = (int) objectInputStream.readObject();
                            int height = (int) objectInputStream.readObject();
                            remoteScreen = new Screen(width, height);
                            remoteScreen.setScreenPosition("RIGHT");
                            Platform.runLater(() -> labelScreenSize.setText(width + " x " + height + "px"));
                            break;
                    }
                } else {
                    Platform.runLater(() -> labelScreenSize.setText("N/A"));
                    System.out.println("Disconnected");
                    break;
                }
            }
        } catch (EOFException e) {
            onClickBtnDisconnect(null);
            Platform.runLater(() -> showDialogMessage("Connection Lost: Possible reasons\n\t- The server has reset\n\t- You are not connected to any network"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            connectionClosed();
        }
    }

    private void delayChangeStatus(String message, String original) {
        labelStatus.setText(message);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> labelStatus.setText(original));
            }
        };
        timer.schedule(task, 3000);
    }

    //this method is called to send message to server (Desktop)
    public static void sendMessageToServer(Object message) {
        try {
            if (clientSocket == null) return;
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            connectionClosed();
        }
    }

    //Called after RemoteScreen is selected and mouse is moved on RemoteScreen
    private final OnMouseMoveListener onMouseMoveListener = new OnMouseMoveListener() {
        @Override
        public void onMove(int x, int y) {
            sendMessageToServer("MOUSE_MOVE");
            sendMessageToServer(x);
            sendMessageToServer(y);

            if (localScreen.getScreenPosition().equals("LEFT") && x < 2) {
                startLocalMouseTracker(true);
                robot.mouseMove((int) (localScreen.screenWidth - SHIFT_DIST), y);
            } else if (localScreen.getScreenPosition().equals("RIGHT") && x > remoteScreen.screenWidth - 2) {
                startLocalMouseTracker(true);
                robot.mouseMove(SHIFT_DIST, y);
            }
        }
    };

    //Called when RemoteScreen is about to be selected
    private OnChangeScreenListener changeScreenListener = new OnChangeScreenListener() {
        @Override
        public void changeScreens(double y) {
            if (remoteScreen.getScreenPosition().equals("RIGHT"))
                robot.mouseMove(SHIFT_DIST, (int) y);
            else
                robot.mouseMove((int) (remoteScreen.screenWidth - SHIFT_DIST), (int) y);
            startLocalMouseTracker(false);
        }
    };

    // Remove RemoteMotionListener and add LocalMotionListener or vice versa
    private void startLocalMouseTracker(boolean toStart) {
        remoteScreen.setSelected(!toStart);
        if (toStart) {
            primaryStage.show();
            primaryStage.addEventFilter(MouseEvent.ANY, mouseMotionHandler);
            primaryStage.addEventFilter(KeyEvent.ANY, keyboardEventHandler);
            System.out.println("LocalScreen selected");
        } else {
            primaryStage.hide();
            primaryStage.removeEventFilter(MouseEvent.ANY, mouseMotionHandler);
            primaryStage.removeEventFilter(KeyEvent.ANY, keyboardEventHandler);
            System.out.println("RemoteScreen selected");
        }
    }

    //Initialize local PC Overlay frame
    private static Stage initializeOverlayStage() {
        Stage primaryStage = new Stage();
        GridPane root = new GridPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 512, 300));
        primaryStage.setFullScreen(true);
        return primaryStage;
    }

    public static void connectionClosed() {
        try {
            objectInputStream.close();
            clientSocket.close();
            objectOutputStream.close();
        } catch (IOException | NullPointerException ignored) {}
    }
}
