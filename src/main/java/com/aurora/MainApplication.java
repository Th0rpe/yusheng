package com.aurora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/gui.fxml"));
        primaryStage.setTitle("漏洞检测工具");
        primaryStage.getIcons().add(new Image("avatar.jpg"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
