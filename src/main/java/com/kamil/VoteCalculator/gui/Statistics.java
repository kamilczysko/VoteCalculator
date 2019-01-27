package com.kamil.VoteCalculator.gui;

import com.itextpdf.text.DocumentException;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.candidate.CandidateTable;
import com.kamil.VoteCalculator.model.party.PartyTable;
import com.kamil.VoteCalculator.model.user.UserService;
import com.kamil.VoteCalculator.utils.DocumentGenerator;
import com.kamil.VoteCalculator.utils.PDFCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component public class Statistics {

	private final Logger logger = LoggerFactory.getLogger(Statistics.class);

	@Autowired private ApplicationContext context;
	@Autowired private UserService userService;
	@Autowired private CandidateService candidateService;
	@Autowired private DocumentGenerator documentGenerator;

	@FXML private StackedBarChart<String, Number> candidateChart;
	@FXML private BarChart<String, Number> partyChart;
	@FXML private TableView summaryTable;
	@FXML private TableColumn<CandidateTable, String> candidateColumn;
	@FXML private TableColumn<CandidateTable, Integer> voteColumn;
	@FXML private TableColumn<CandidateTable, String> partyColumn;
	@FXML private Tab statTab;
	@FXML private Tab chartTab;
	@FXML private TableView partyTable;
	@FXML private TableColumn partyNamesColumn;
	@FXML private TableColumn partyVotesColumn;
	@FXML private Label voidedVotesLabel;
	@FXML private Label disallowedVotesLabel;

	private int voidedVotes = 0;
	private int disallowedVotes = 0;

	private final ObservableList<CandidateTable> candidatesData = FXCollections.observableArrayList();
	private final ObservableList<PartyTable> partiesData = FXCollections.observableArrayList();
	private MultiValueMap<String, Candidate> allCandidatesMap;

	public void initialize() {
		candidateColumn.setCellValueFactory(new PropertyValueFactory<CandidateTable, String>("name"));

		partyColumn.setCellValueFactory(new PropertyValueFactory<CandidateTable, String>("party"));

		voteColumn.setCellValueFactory(new PropertyValueFactory<CandidateTable, Integer>("votes"));

		partyNamesColumn.setCellValueFactory(new PropertyValueFactory<PartyTable, String>("name"));

		partyVotesColumn.setCellValueFactory(new PropertyValueFactory<PartyTable, Integer>("votes"));

		summaryTable.setItems(candidatesData);
		partyTable.setItems(partiesData);

		refreshAll();
	}

	@FXML public void refresh() {
		refreshAll();
	}

	private void refreshAll() {
		logger.info("REFRESH STATS");
		setVoidedVotes();
		setDisallowedVotes();
		setCandidateData();
		setCandidateChart();
		setPartyChart();
	}

	@FXML private void logout() {
		lo();
	}

	private void lo() {
		Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
		VoteCalculatorApplication.stage.setWidth(loginWindow.getWidth());
		VoteCalculatorApplication.stage.setHeight(loginWindow.getHeight());
		VoteCalculatorApplication.stage.setScene(loginWindow);
		SecurityContextHolder.clearContext();
	}

	@FXML private void exportPDF() {
		candidateChart.setAnimated(false);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("summary.pdf");
		fileChooser.setTitle("Save summary in pdf");
		File file = fileChooser.showSaveDialog(VoteCalculatorApplication.stage);

		try {
			toPDF(file);
		} catch (Exception e) {
			logger.error("ExportPDF", e);
		}
	}

	private void toPDF(File file) throws DocumentException, IOException {

		PDFCreator pdf = documentGenerator.createPDF(file);

		int[] badVotes = { this.voidedVotes, this.disallowedVotes };
		String[] headers = { "*Voided votes", "Disallowed votes" };

		pdf.prepDocument().mainHeader("Vote summary").subHeader("Candidates summary")
				.tableWithCandidates(candidatesData, "Name", "Party", "Votes").subHeader("Parties summary")
				.tableOfParties(partiesData, "Party", "Votes").subHeader("Voided votes summary")
				.keyIntValTable(badVotes, headers).addTipText("*Disallowed votes included in voided votes").nextPage()
				.mainHeader("Charts").subHeader("Candidates chart").chart(candidateChart).subHeader("Parties chart")
				.chart(partyChart).finishDocument();

	}

	@FXML private void exportCSV() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("summary.csv");
		fileChooser.setTitle("Export summary to csv");
		File file = fileChooser.showSaveDialog(VoteCalculatorApplication.stage);
		documentGenerator.saveCandidatesToCSV(file, candidatesData);
	}

	private void setCandidateData() {
		allCandidatesMap = candidateService.getAllCandidatesToMap();
		List<PartyTable> partiesList = new ArrayList<>();
		List<CandidateTable> candidateList = new ArrayList<>();
		Set<String> strings = allCandidatesMap.keySet();

		strings.forEach(key -> {
			AtomicInteger partyVotes = new AtomicInteger();
			List<Candidate> candidates = allCandidatesMap.get(key);

			candidates.forEach(c -> {
				candidateList.add(new CandidateTable(c));
				partyVotes.addAndGet(c.getVotes());
			});

			partiesList.add(new PartyTable(key, partyVotes.get()));
		});

		partiesData.clear();
		partiesData.addAll(partiesList);

		candidatesData.clear();
		candidatesData.addAll(candidateList);
	}

	private void setCandidateChart() {
		candidateChart.getData().clear();

		Set<String> strings = allCandidatesMap.keySet();
		for (String k : strings) {
			List<Candidate> candidates = allCandidatesMap.get(k);
			XYChart.Series series1 = new XYChart.Series();
			series1.setName(k);

			candidates.forEach(c -> {
				series1.getData().add(new XYChart.Data(c.getName(), c.getVotes()));
			});

			candidateChart.getData().add(series1);
		}
	}

	private void setVoidedVotes() {
		voidedVotes = userService.getBadVotes();
		voidedVotesLabel.setText(String.valueOf(voidedVotes));
	}

	private void setDisallowedVotes() {
		disallowedVotes = userService.getDisallowedVotes();
		disallowedVotesLabel.setText(String.valueOf(disallowedVotes));
	}

	@FXML private void refreshMenu() {
		refreshAll();
	}

	@FXML private void logoutMenu() {
		lo();
	}

	@FXML private void close() {
		System.exit(0);
	}

	private void setPartyChart() {
		partyChart.getData().clear();

		this.partiesData.forEach(pt -> {
			XYChart.Series series = new XYChart.Series();
			series.setName(pt.getName());
			series.getData().add(new XYChart.Data(pt.getName(), pt.getVotes()));
			partyChart.getData().add(series);
		});
	}
}
