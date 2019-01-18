package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.user.User;
import com.kamil.VoteCalculator.model.user.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;
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
        System.out.println("################ VOTE LIST");
        loadCandidates();
    }

    @FXML
    public void logout() {
        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
        SecurityContextHolder.clearContext();
        VoteCalculatorApplication.stage.setScene(loginWindow);
        voted.clear();
    }

    public void loadCandidates() {

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
    private void vote(ActionEvent event) {
        boolean badVote = voted.size() != 1;
        if (alert(badVote)) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                long currentPrincipalName = Long.parseLong(authentication.getName());
                User voted = userService.voted(currentPrincipalName, badVote);
                if (!badVote && !voted.isDisallowed()) {
                    for (Candidate c : this.voted) {
                        candidateService.vote(c, this.voted.size() > 1);
                    }
                }

                this.voted.clear();
                Scene statScene = context.getBean("loadStatisticsWindow", Scene.class);
                Stage stage  = (Stage)(((Node) event.getSource()).getScene()).getWindow();
                stage.getScene().getRoot().prefWidth(1100.0);
                stage.getScene().getRoot().prefHeight(450.0);
                stage.setScene(statScene);
                stage.sizeToScene();
//                stage.show();
//                VoteCalculatorApplication.stage.setScene(statScene);
//                VoteCalculatorApplication.stage.sizeToScene();

            } catch (Exception e) {
                System.out.println(e);
                alertVoted();
            }
        }
    }

    private boolean alert(boolean badVote) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm vote!");
        alert.setHeaderText(null);
        if (badVote)
            alert.setContentText("Your vote won't count.\n Are you sure?");
        else
            alert.setContentText("Is it your last word?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private void alertVoted() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Vote Error!");
        alert.setHeaderText(null);
        alert.setContentText("You have voted already!");

        alert.showAndWait();
    }

}
