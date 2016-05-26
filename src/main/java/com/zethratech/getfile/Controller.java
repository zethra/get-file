package com.zethratech.getfile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import net.glxn.qrgen.javase.QRCode;
import org.eclipse.jetty.server.Server;

import java.io.File;

public class Controller {

    public Server server;
    public HTTPHandler handler;

    public void init(Server server, HTTPHandler handler) {
        this.server = server;
        this.handler = handler;
    }

    @FXML
    private ImageView qrCode;

    @FXML
    private void handleSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile == null || !selectedFile.exists())
            return;
        if(!server.isStarted()) {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        handler.setFile(selectedFile);
        qrCode.setImage(new Image(getQRCode(selectedFile.getName())));
    }

    public String getQRCode(String fileName) {
        File file = QRCode.from("http://" + handler.getInterfaces().get(0).getAdress() + ":8080/get?file=" + fileName)
                .file();
        return file.toURI().toString();
    }
}
