package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class VoteList {

    @Autowired
    CandidateService candidateService;

    @FXML
    VBox voteBox;

    @FXML
    Button voteButton;

    @FXML
    VBox mainBox;

    List<Node> allNodes = new ArrayList<>();


    private List<Candidate> voted = new ArrayList<Candidate>();

    public void initialize() {

        MultiValueMap<String, Candidate> allCandidates = candidateService.getAllCandidatesToMap();
        Set<String> longs = allCandidates.keySet();

        for (String key : longs) {
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
        System.out.println("VOTE!!!");
        for (Candidate c : voted) {
            candidateService.vote(c);
        }
        voted.clear();
        voteBox.getChildren().removeAll(allNodes);
    }
}
