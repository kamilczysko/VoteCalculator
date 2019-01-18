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

        boolean disallowed = this.disallowed.isDisallowed(peselField.getText());

        if (disallowed)
            alertDisallowed();

        registerUser(disallowed);

        firstNameField.clear();
        secondNameField.clear();
        peselField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        registerButton.setDisable(true);
    }

    private void registerUser(boolean disallowed) {

        String peselHash = Hashing.sha256()
                .hashString(peselField.getText(), StandardCharsets.UTF_8)
                .toString();

        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setSecondName(secondNameField.getText());
        user.setPassword(passwordEncoder.encode(passwordField.getText()));
        user.setDisallowed(disallowed);
        user.setPesel(peselHash);
        user.setRoles(roles.get("unvoted"));

        try {
            userService.registerNewUser(user);
        }catch (Exception e){
            System.out.println(e);
            alertRegister();
        }
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

    private void alertDisallowed() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Register warning!");
        alert.setHeaderText(null);
        alert.setContentText("Pesel disallowed!\nYour vote will be voided.");

        alert.showAndWait();
    }

    private void alertRegister() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Register warning!");
        alert.setHeaderText(null);
        alert.setContentText("Cannot register user.");

        alert.showAndWait();
    }

}
