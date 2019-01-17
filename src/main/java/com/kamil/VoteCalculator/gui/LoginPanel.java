package com.kamil.VoteCalculator.gui;

import com.google.common.hash.Hashing;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginPanel {

    @Autowired
    ConfigurableApplicationContext context;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @FXML
    PasswordField passwordField;

    @FXML
    TextField peselField;

    @FXML
    private void login() {
        loginDB(peselField.getText(), passwordField.getText());
        System.out.println(passwordField.getText() + " - " + peselField.getText());
    }

    private void loginDB(String pesel, String password) {

        String peselHash = Hashing.sha256()
                .hashString(pesel, StandardCharsets.UTF_8)
                .toString();

        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(peselHash, password);
        System.out.println(request.isAuthenticated() + " - " + request.getAuthorities());
        try {
            Authentication ath = authenticationManager.authenticate(request);
            Authentication authResult = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            Stage loadVoteWindow = context.getBean("loadVoteWindow", Stage.class);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            loadVoteWindow.setTitle("Voting panel - "+authentication.getPrincipal());
            loadVoteWindow.show();
        } catch (Exception ex) {
            alert();
            System.out.println(ex);
        }

    }

    private void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login error!");
        alert.setHeaderText(null);
        alert.setContentText("Bad credentials");

        alert.showAndWait();
    }
}
