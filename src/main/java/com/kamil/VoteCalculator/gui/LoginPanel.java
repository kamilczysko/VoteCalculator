package com.kamil.VoteCalculator.gui;

import com.google.common.hash.Hashing;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.Disallowed;
import com.kamil.VoteCalculator.model.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component public class LoginPanel {

	private final Logger logger = LoggerFactory.getLogger(LoginPanel.class);

	@Autowired private ConfigurableApplicationContext context;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private UserService userService;
	@Autowired private Disallowed disallowed;

	@FXML PasswordField passwordField;
	@FXML TextField peselField;

	@FXML private void login() {

		loginDB(peselField.getText(), passwordField.getText());
	}

	private void loginDB(String pesel, String password) {

		String peselHash = Hashing.sha256().hashString(pesel, StandardCharsets.UTF_8).toString();

		if (disallowed.isDisallowed(peselField.getText())) {
			alert("You are disallowed to vote!\nYour vote wont count.", false);
			userService.setUserDisallowed(peselHash);
		}

		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(peselHash, password);

		try {
			Authentication authResult = authenticationManager.authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(authResult);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			boolean hasVotedRole = authentication.getAuthorities().stream()
					.anyMatch(r -> r.getAuthority().equals("ROLE_voted"));

			if (hasVotedRole) {
				changeToStatScene();
			} else {
				changeToVoteScene();
			}

		} catch (Exception ex) {
			logger.error("loginDB", ex);
			alert("Bad credentials!", true);
		}

	}

	private void changeToVoteScene() {
		Scene voteScene = context.getBean("loadVoteWindow", Scene.class);
		VoteCalculatorApplication.stage.setScene(voteScene);
		VoteCalculatorApplication.stage.sizeToScene();
	}

	private void changeToStatScene() {
		Scene statScene = context.getBean("loadStatisticsWindow", Scene.class);
		VoteCalculatorApplication.stage.setScene(statScene);
		VoteCalculatorApplication.stage.sizeToScene();
	}

	private void alert(String msg, boolean error) {
		Alert alert;

		if (error) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Login error!");
		} else {
			alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Login warning!");
		}
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
}
