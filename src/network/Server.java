package network;

import inputcontrol.MouseKeyboardControl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import viewcontrollers.ServerTabController;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static viewcontrollers.ServerTabController.*;
import static utils.Utils.showDialogMessage;

public class Server {

    private Label connectionStatusLabel;
    private MouseKeyboardControl mouseControl;

    public void connect(javafx.scene.control.Button resetButton, Label connectionStatusLabel) {
        this.connectionStatusLabel = connectionStatusLabel;

        mouseControl = new MouseKeyboardControl();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Timeline timerLB = blinkLabel();

        try {
            updateConnectionLabel("Waiting for connection...");
            clientSocket = serverSocket.accept();
            clientSocket.setSendBufferSize(32 * 1024);
            clientSocket.setReceiveBufferSize(32 * 1024);

            resetButton.setDisable(false);

            updateConnectionLabel((clientSocket.getRemoteSocketAddress()).toString().replace("/", "") + " Connected");
            timerLB.play();

            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(ServerTabController.inputStream);

            sendMessageToClient("SCREEN_DIMENSIONS");
            sendMessageToClient((int) screenSize.getWidth());
            sendMessageToClient((int) screenSize.getHeight());

            while (true) {
                try {
                    String message = (String) ServerTabController.objectInputStream.readObject();
                    if (message != null) {
                        checkMessage(message);
                    } else {
                        //remote connection closed
                        closeConnection();
                        timerLB.stop();
                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    closeConnection();
                    timerLB.stop();
                    break;
                }
            }
        } catch (IOException e) {
            showDialogMessage("Error in initializing server");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        connectionClosed();
        updateConnectionLabel("Disconnected");
    }

    private void checkMessage(String message) throws IOException, ClassNotFoundException {
        switch (message) {
            case "MOUSE_MOVE":
                int x = (int) objectInputStream.readObject();
                int y = (int) objectInputStream.readObject();
                System.out.println("Moved mouse to: " + x + "px, " + y + "px");
                mouseControl.mouseMove(x, y);
                break;
            case "LEFT_MOUSE_PRESSED":
                mouseControl.mousePress(InputEvent.BUTTON1_MASK);
                break;
            case "LEFT_MOUSE_RELEASED":
                mouseControl.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case "RIGHT_MOUSE_PRESSED":
                mouseControl.mousePress(InputEvent.BUTTON3_MASK);
                break;
            case "RIGHT_MOUSE_RELEASED":
                mouseControl.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            case "KEY_PRESSED":
                KeyCode keyCode = (KeyCode) objectInputStream.readObject();
                mouseControl.keyPress(keyCode.impl_getCode());
                break;
            case "KEY_RELEASED":
                KeyCode keyCodE = (KeyCode) objectInputStream.readObject();
                mouseControl.keyRelease(keyCodE.impl_getCode());
                break;
        }
    }

    private void updateConnectionLabel(String message) {
        Platform.runLater(() -> connectionStatusLabel.setText(message));
    }

    private Timeline blinkLabel() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), evt -> connectionStatusLabel.setVisible(false)),
                new KeyFrame(Duration.seconds(0.5), evt -> connectionStatusLabel.setVisible(true))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }
}
