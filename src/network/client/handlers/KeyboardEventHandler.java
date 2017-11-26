package network.client.handlers;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import static viewcontrollers.ClientTabController.sendMessageToServer;

public class KeyboardEventHandler implements EventHandler<KeyEvent> {
    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            sendMessageToServer("KEY_PRESSED");
            sendMessageToServer(event.getCode());
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            sendMessageToServer("KEY_RELEASED");
            sendMessageToServer(event.getCode());
        }
    }
}