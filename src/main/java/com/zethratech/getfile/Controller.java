package com.zethratech.getfile;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import net.glxn.qrgen.javase.QRCode;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.net.SocketException;
import java.util.Optional;

public class Controller {

    public Server server;
    public HTTPHandler handler;

    public void init(Server server, HTTPHandler handler) {
        this.server = server;
        this.handler = handler;
    }

    @FXML
    public ImageView qrCode;

    @FXML
    public void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile == null || !selectedFile.exists())
            return;
        if (!server.isStarted()) {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        handler.setFile(selectedFile);
        qrCode.setImage(new Image(getQRCode(selectedFile.getName())));
    }

    @FXML
    public void handleSettings() {
        if (handler.getInterfaces() == null) {
            try {
                handler.generateInterfaceList();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        ChoiceDialog<IpInterface> dialog = new ChoiceDialog<>(handler.getDefaultInterface(), handler.getInterfaces());
        dialog.setTitle("Settings");
        dialog.setHeaderText("Settings");
        dialog.setContentText("Select an interface:");
        Optional<IpInterface> result = dialog.showAndWait();
        result.ifPresent(selectedInterface -> handler.setDefaultInterface(selectedInterface));
    }

    public String getQRCode(String fileName) {
        File file = QRCode.from("http://" + handler.getInterfaces().get(0).getAdress() + ":8080/get?file=" + fileName)
                .file();
        return file.toURI().toString();
    }
}
