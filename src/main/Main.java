package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        Parent root = FXMLLoader.load(getClass().getResource("main_ui.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 512, 300));
        primaryStage.show();

//        primaryStage.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
//            Point point = MouseInfo.getPointerInfo().getLocation();
//            System.out.printf("Point(%s, %s)\n", point.getX(), point.getY());
//        });
    }
}
