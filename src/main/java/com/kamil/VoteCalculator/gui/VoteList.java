package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.user.User;
import com.kamil.VoteCalculator.model.user.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.applet.AppletContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class VoteList {

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private UserService userService;
    @Autowired
    ApplicationContext context;

    @FXML
    private VBox voteBox;

    @FXML
    private Button voteButton;

    @FXML
    private VBox mainBox;

    private List<Node> allNodes = new ArrayList<>();


    private List<Candidate> voted = new ArrayList<Candidate>();

    public void initialize() {
        System.out.println(candidateService);
        loadCandidates();
    }

    @FXML
    public void logout() {
        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
        VoteCalculatorApplication.stage.setScene(loginWindow);
    }

    public void loadCandidates() {

//        this.candidateService = candidateService;

        MultiValueMap<String, Candidate> allCandidates = candidateService.getAllCandidatesToMap();
        Set<String> longs = allCandidates.keySet();

        for (String key : longs) {
            System.out.println(key);
            Label partyNameLabel = new Label(key + ":");
            partyNameLabel.setFont(Font.font("Amble CN", FontWeight.LIGHT, 16));
            partyNameLabel.setPadding(new Insets(0, 0, 2, 0));
            voteBox.getChildren().add(partyNameLabel);
            allNodes.add(partyNameLabel);

            List<Candidate> candidates = allCandidates.get(key);
            for (Candidate c : candidates) {
                CheckBox candidateBox = new CheckBox();
                candidateBox.setText(c.getName());
                candidateBox.setFont(Font.font("Amble CN", FontWeight.LIGHT, 12));
                candidateBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue)
                            voted.add(c);
                        else
                            voted.remove(c);
                        System.out.println(voted);
                    }
                });

                allNodes.add(candidateBox);
                voteBox.getChildren().add(candidateBox);
            }
        }
    }

    @FXML
    private void vote() {
        if (alert()) {
            System.out.println("VOTE!!!");
            for (Candidate c : voted) {
                candidateService.vote(c, voted.size() > 1);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            long currentPrincipalName = Long.parseLong(authentication.getName());
            userService.voted(currentPrincipalName);
            voted.clear();
        }
    }

    private boolean alert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm vote!");
        alert.setHeaderText(null);
        alert.setContentText("Confirm vote!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

}
