package com.zethratech.getfile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.jetty.server.Server;

public class Main extends Application {

    public int port = 8080;
    public Server server;
    public HTTPHandler handler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        server = new Server(port);
        handler = new HTTPHandler();
        server.setHandler(handler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.init(server, handler);
        handler.generateInterfaceList();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Get File");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (!server.isStopped()) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
