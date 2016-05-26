package com.zethratech.getfile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.eclipse.jetty.server.Server;

import java.util.List;

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

    }
}
