package network.client.handlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import network.Screen;
import network.client.interfaces.OnChangeScreenListener;
import network.client.interfaces.OnMouseMoveListener;

import static viewcontrollers.ClientTabController.sendMessageToServer;

public class MouseEventHandler implements EventHandler<MouseEvent> {

    private final Screen remoteScreen, localScreen;
    private OnMouseMoveListener onMouseMoveListener;
    private OnChangeScreenListener onChangeScreenListener;

    public MouseEventHandler(Screen localScreen, Screen remoteScreen) {
        this.remoteScreen = remoteScreen;
        this.localScreen = localScreen;
    }

    public void setOnMouseMoveListener(OnMouseMoveListener onMouseMoveListener) {
        this.onMouseMoveListener = onMouseMoveListener;
    }

    public void setOnScreenChangeListener(OnChangeScreenListener onChangeScreenListener) {
        this.onChangeScreenListener = onChangeScreenListener;
    }

    @Override
    public void handle(MouseEvent event) {
        switch (event.getEventType().getName()) {
            case "MOUSE_PRESSED":
                if (event.getButton() == MouseButton.PRIMARY)
                    sendMessageToServer("LEFT_MOUSE_PRESSED");
                else if (event.getButton() == MouseButton.SECONDARY)
                    sendMessageToServer("RIGHT_MOUSE_PRESSED");
                break;
            case "MOUSE_RELEASED":
                if (event.getButton() == MouseButton.PRIMARY)
                    sendMessageToServer("LEFT_MOUSE_RELEASED");
                else if (event.getButton() == MouseButton.SECONDARY)
                    sendMessageToServer("RIGHT_MOUSE_RELEASED");
                break;
            case "MOUSE_MOVED":
                if (remoteScreen.getScreenPosition().equals("LEFT") && event.getX() < 2)
                    onChangeScreenListener.changeScreens(event.getY());
                else if (remoteScreen.getScreenPosition().equals("RIGHT") && event.getX() > localScreen.screenWidth - 2)
                    onChangeScreenListener.changeScreens(event.getY());
                else {
                    int distX = calcPixelDistance(event.getX(), localScreen.screenWidth, remoteScreen.screenWidth);
                    int distY = calcPixelDistance(event.getY(), localScreen.screenHeight, remoteScreen.screenHeight);
                    onMouseMoveListener.onMove(distX, distY);
                }
                break;
        }
    }

    private int calcPixelDistance(double mouse, double local, double remote) {
        return (int) Math.round((mouse / local) * remote);
    }
}