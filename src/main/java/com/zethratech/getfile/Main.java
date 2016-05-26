package com.zethratech.getfile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.glxn.qrgen.javase.QRCode;
import org.eclipse.jetty.server.Server;

import java.io.File;

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
        Scene scene = new Scene(root);
        primaryStage.setTitle("Get File");
        primaryStage.setScene(scene);



        primaryStage.show();
    }

    public String getQRCode(String fileName) {
        File file = QRCode.from("http://" + handler.getInterfaces().get(0).getAdress() + ":8080/get?file=" + fileName)
                .file();
        return file.getAbsolutePath();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
