package com.sentomero.sufeeds.javasufeeds.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginContoller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick()
    {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}