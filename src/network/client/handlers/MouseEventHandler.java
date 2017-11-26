package network.client.handlers;

import javafx.event.EventHandler;
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
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            System.out.println("Pressed|"+event.isPrimaryButtonDown());
            if (event.isPrimaryButtonDown())
                sendMessageToServer("LEFT_MOUSE_PRESSED");
            else if (event.isSecondaryButtonDown())
                sendMessageToServer("RIGHT_MOUSE_PRESSED");
        }
        if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (event.isPrimaryButtonDown())
                sendMessageToServer("LEFT_MOUSE_RELEASED");
            else if (event.isSecondaryButtonDown())
                sendMessageToServer("RIGHT_MOUSE_RELEASED");
        }
        if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
            if (remoteScreen.getScreenPosition().equals("LEFT") && event.getX() < 2)
                onChangeScreenListener.changeScreens(event.getY());
            else if (remoteScreen.getScreenPosition().equals("RIGHT") && event.getX() > localScreen.screenWidth - 2)
                onChangeScreenListener.changeScreens(event.getY());
            else {
                int distX = calcPixelDistance(event.getX(), localScreen.screenWidth, remoteScreen.screenWidth);
                int distY = calcPixelDistance(event.getY(), localScreen.screenHeight, remoteScreen.screenHeight);
                onMouseMoveListener.onMove(distX, distY);
            }
        }
    }

    private int calcPixelDistance(double mouse, double local, double remote) {
        return (int) Math.round((mouse / local) * remote);
    }
}