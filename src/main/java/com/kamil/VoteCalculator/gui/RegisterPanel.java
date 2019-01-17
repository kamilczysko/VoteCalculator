package com.kamil.VoteCalculator.gui;

import com.google.common.hash.Hashing;
import com.kamil.VoteCalculator.model.Disallowed;
import com.kamil.VoteCalculator.model.role.Roles;
import com.kamil.VoteCalculator.model.role.RolesService;
import com.kamil.VoteCalculator.model.user.User;
import com.kamil.VoteCalculator.model.user.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RegisterPanel {

    @Autowired
    private Disallowed disallowed;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UserService userService;

    private Map<String, Roles> roles;

    @FXML
    private Button registerButton;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField secondNameField;

    @FXML
    private TextField peselField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;


    @FXML
    private void register() {

        if (disallowed.isDisallowed(peselField.getText())) {
            System.out.println("disallowed pesel");
            alert();
            return;
        }

        registerUser();

        firstNameField.clear();
        secondNameField.clear();
        peselField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        registerButton.setDisable(true);
    }

    private void registerUser() {

        String peselHash = Hashing.sha256()
                .hashString(peselField.getText(), StandardCharsets.UTF_8)
                .toString();

        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setSecondName(secondNameField.getText());
        user.setPassword(passwordEncoder.encode(passwordField.getText()));
        user.setPesel(peselHash);
        user.setRoles(roles.get("unvoted"));

        userService.registerNewUser(user);
    }

    public void initialize() {
        registerButton.setDisable(true);
        confirmPasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                if (newValue.equals(passwordField.getText())) {
                    registerButton.setDisable(false);
                }
            }
        });

        roles = rolesService.getRolesMap();
    }

    private void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Register error!");
        alert.setHeaderText(null);
        alert.setContentText("Pesel forbidden!");

        alert.showAndWait();
    }

}
