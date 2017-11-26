package utils;

import javafx.scene.control.Alert;

/**
 * Created by Forntoh on 25-Nov-17.
 */
public class Utils {
    public static void showDialogMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
