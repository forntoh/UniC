package network.client.handlers;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;

import static viewcontrollers.ClientTabController.sendMessageToServer;

public class KeyboardEventHandler implements EventHandler<KeyEvent> {
    @Override
    public void handle(KeyEvent event) {
        final EventType<?> type = event.getEventType();
        if (type == KeyEvent.KEY_PRESSED) {
            sendMessageToServer("KEY_PRESSED");
            sendMessageToServer(event.getCode());
        } else if (type == KeyEvent.KEY_RELEASED) {
            sendMessageToServer("KEY_RELEASED");
            sendMessageToServer(event.getCode().impl_getCode());
        }
    }
}