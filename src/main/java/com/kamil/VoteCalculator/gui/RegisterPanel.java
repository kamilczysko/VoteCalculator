package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.model.Disallowed;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterPanel {

    @Autowired
    Disallowed disallowed;

    @FXML
    Button registerButton;

    @FXML
    TextField firstNameField;

    @FXML
    TextField secondNameField;

    @FXML
    TextField peselField;

    @FXML
    PasswordField passwordField;

    @FXML
    PasswordField confirmPasswordField;


    @FXML
    private void register() {

        if(disallowed.isDisallowed(peselField.getText())){
            System.out.println("disallowed pesel");
            return;
        }

        firstNameField.clear();
        secondNameField.clear();
        peselField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        registerButton.setDisable(true);
    }

    public void initialize(){
        registerButton.setDisable(true);
        confirmPasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                if(newValue.equals(passwordField.getText())){
                    registerButton.setDisable(false);
                }
            }
        });
    }
}
