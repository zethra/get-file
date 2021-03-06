package com.zethratech.getfile;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import net.glxn.qrgen.javase.QRCode;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Optional;

public class Controller {

    public Server server;
    public HTTPHandler handler;
    private boolean isQrCode = false;

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
                qrCode.setImage(new Image(String.valueOf(getClass().getResource("/file-upload-icon.png"))));
                return;
            }
        }
        handler.setFile(selectedFile);
        qrCode.setImage(new Image(getQRCode(selectedFile.getName())));
        isQrCode = true;
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
        if(isQrCode)
            qrCode.setImage(new Image(getQRCode(handler.getFile().getName())));
    }

    public String getQRCode(String fileName) {
        String url = null;
        try {
            url = URLEncoder.encode(fileName, "ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = QRCode.from("http://" + handler.getDefaultInterface().getAddress() + ":8080/get?file=" + url)
                .file();
        return file.toURI().toString();
    }
}
