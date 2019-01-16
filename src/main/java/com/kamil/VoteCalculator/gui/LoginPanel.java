package com.kamil.VoteCalculator.gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class LoginPanel {

    @FXML
    PasswordField passwordField;

    @FXML
    TextField peselField;

    @FXML
    private void login(){
        System.out.println(passwordField.getText()+ " - "+peselField.getText());
        
    }



}
