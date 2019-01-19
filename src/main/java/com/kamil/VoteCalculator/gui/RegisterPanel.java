package com.kamil.VoteCalculator.gui;

import com.google.common.hash.Hashing;
import com.kamil.VoteCalculator.model.Disallowed;
import com.kamil.VoteCalculator.model.role.Roles;
import com.kamil.VoteCalculator.model.role.RolesService;
import com.kamil.VoteCalculator.model.user.User;
import com.kamil.VoteCalculator.model.user.UserService;
import com.kamil.VoteCalculator.utils.TimeUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RegisterPanel {

    private final Logger logger = LoggerFactory.getLogger(RegisterPanel.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UserService userService;

    private Map<String, Roles> roles = null;

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
    private Label peselLog;

    @FXML
    private void register() {

        boolean isRegistred = registerUser();
        if (isRegistred) {
            firstNameField.clear();
            secondNameField.clear();
            peselField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            registerButton.setDisable(true);
        }
    }

    private boolean registerUser() {

        if (firstNameField.getText().isEmpty() || secondNameField.getText().isEmpty() || !(passwordField.getText().equals(confirmPasswordField.getText()))) {
            warning("Fill all fields properly!");
            return false;
        }

        String peselHash = Hashing.sha256()
                .hashString(peselField.getText(), StandardCharsets.UTF_8)
                .toString();

        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setSecondName(secondNameField.getText());
        user.setPassword(passwordEncoder.encode(passwordField.getText()));
        user.setPesel(peselHash);
        user.setRoles(roles.get("unvoted"));

        try {
            userService.registerNewUser(user);
            confirm();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            warning("Cannot register user");
            return false;
        }
    }

    public void initialize() {
        registerButton.setDisable(true);

        confirmPasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(passwordField.getText())) {
                    registerButton.setDisable(false);
                } else {
                    registerButton.setDisable(true);
                }
            }
        });

        peselField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                boolean disable = true;
                if (!newValue.matches("[0-9]*")) {
                    peselField.setText(oldValue);
                    newValue = oldValue;
                    disable = true;
                } else if (newValue.length() > 11) {
                    disable = true;
                    peselLog("Pesel too long!", true);
                } else if (newValue.length() < 11) {
                    peselLog.setText("");
                }

                if (newValue.length() == 11) {
                    if (TimeUtils.isAdult(newValue.substring(0, 6))) {
                        peselLog("Pesel ok!", false);
                        disable = false;
                    } else {
                        peselLog("You are to young to vote!", true);
                    }
                }

                passwordField.setDisable(disable);
            }
        });

        passwordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() >= 5) {
                    confirmPasswordField.setDisable(false);
                } else {
                    confirmPasswordField.setDisable(true);
                }
            }
        });

        roles = rolesService.getRolesMap();
        if (roles == null) {
            logger.error("Database probably has been not initialized properly. Run application from command line with argument\"first\"");
            System.exit(0);
        }
    }

    private void peselLog(String msg, boolean warn) {
        if (warn)
            peselLog.setTextFill(Color.RED);
        else
            peselLog.setTextFill(Color.GREEN);
        peselLog.setText(msg);
    }

    private void warning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Register warning!");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void confirm() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Register");
        alert.setHeaderText(null);
        alert.setContentText("Account created");

        alert.showAndWait();
    }

}
