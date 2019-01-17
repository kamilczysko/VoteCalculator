package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.candidate.CandidateTable;
import com.kamil.VoteCalculator.model.candidate.PartyTable;
import com.kamil.VoteCalculator.model.user.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class Statistics {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserService userService;
    @Autowired
    private CandidateService candidateService;

    @FXML
    StackedBarChart<String, Number> chart;
    @FXML
    TableView summaryTable;
    @FXML
    TableColumn<CandidateTable, String> candidateColumn;
    @FXML
    TableColumn<CandidateTable, Integer> voteColumn;
    @FXML
    TableColumn<CandidateTable, String> partyColumn;
    @FXML
    Tab statTab;
    @FXML
    Tab chartTab;

    @FXML
    TableView partyTable;
    @FXML
    TableColumn partyNamesColumn;
    @FXML
    TableColumn partyVotesColumn;

    @FXML
    Label voidedVotes;

    final ObservableList<CandidateTable> candidatesData = FXCollections.observableArrayList();
    final ObservableList<PartyTable> partiesData = FXCollections.observableArrayList();


    public void initialize() {
        candidateColumn.setCellValueFactory(
                new PropertyValueFactory<CandidateTable, String>("name"));

        partyColumn.setCellValueFactory(
                new PropertyValueFactory<CandidateTable, String>("party"));

        voteColumn.setCellValueFactory(
                new PropertyValueFactory<CandidateTable, Integer>("votes"));

        partyNamesColumn.setCellValueFactory(
                new PropertyValueFactory<PartyTable, String>("name"));

        partyVotesColumn.setCellValueFactory(
                new PropertyValueFactory<PartyTable, Integer>("votes"));


        summaryTable.setItems(candidatesData);
        partyTable.setItems(partiesData);

        setVoidedVotes();
        getCandidateData();
        getStatisticsForChart();
    }

    @FXML
    public void refresh() {
        setVoidedVotes();
        if (statTab.isSelected())
            getCandidateData();
        else
            getStatisticsForChart();
    }

    @FXML
    private void logout() {
//        getStatistics();
        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
        VoteCalculatorApplication.stage.setScene(loginWindow);
        SecurityContextHolder.clearContext();
    }

    @FXML
    private void exportPDF() {

//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));
//
//        document.open();
//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//        Chunk chunk = new Chunk("Hello World", font);
//
//        document.add(chunk);
//        documentnt.close();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save summary in pdf");
        File file = fileChooser.showSaveDialog(VoteCalculatorApplication.stage);
    }

    private void getCandidateData() {
        MultiValueMap<String, Candidate> allCandidates = candidateService.getAllCandidatesToMap();
        List<PartyTable> partiesList = new ArrayList<>();
        List<CandidateTable> candidateList = new ArrayList<>();

        Set<String> strings = allCandidates.keySet();
        for (String key : strings) {
            int partyVotes = 0;
            List<Candidate> candidates = allCandidates.get(key);
            for (Candidate c : candidates) {
                partyVotes += c.getVotes();
                candidateList.add(new CandidateTable(c));
            }
            partiesList.add(new PartyTable(key, partyVotes));
        }
        partiesData.clear();
        partiesData.addAll(partiesList);

        candidatesData.clear();
        candidatesData.addAll(candidateList);
    }

    private void setVoidedVotes() {
        int badVotes = userService.getBadVotes();
        voidedVotes.setText(String.valueOf(badVotes));
    }

    @FXML
    private void exportCSV() {

    }

    @FXML
    private void close() {
        System.exit(0);
    }

    private void getStatisticsForChart() {
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
