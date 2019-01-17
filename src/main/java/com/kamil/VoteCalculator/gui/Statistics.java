package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class Statistics {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CandidateService candidateService;

    @FXML
    StackedBarChart<String, Number> chart;

    public void initialize() {

    }

    @FXML
    private void logout() {
        getStatistics();
//        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
//        VoteCalculatorApplication.stage.setScene(loginWindow);
//        SecurityContextHolder.clearContext();
    }

    @FXML
    private void exportPDF() {

    }

    @FXML
    private void exportCSV() {

    }

    @FXML
    private void close() {
        System.exit(0);
    }

    Random r = new Random();

    private void getStatistics() {
        chart.getData().clear();

        MultiValueMap<String, Candidate> allCandidates = candidateService.getAllCandidatesToMap();
        Set<String> strings = allCandidates.keySet();
        for (String k : strings) {
            List<Candidate> candidates = allCandidates.get(k);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(k);
            for (Candidate c : candidates) {

                series1.getData().add(new XYChart.Data(c.getName(), c.getVotes()));
            }
            chart.getData().add(series1);

        }
    }

}
