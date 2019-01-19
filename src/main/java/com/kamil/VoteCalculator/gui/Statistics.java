package com.kamil.VoteCalculator.gui;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.candidate.CandidateTable;
import com.kamil.VoteCalculator.model.candidate.PartyTable;
import com.kamil.VoteCalculator.model.user.UserService;
import com.kamil.VoteCalculator.utils.CSVUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class Statistics {

    private final Logger logger = LoggerFactory.getLogger(Statistics.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserService userService;
    @Autowired
    private CandidateService candidateService;

    @FXML
    private StackedBarChart<String, Number> candidateChart;
    @FXML
    private BarChart<String, Number> partyChart;
    @FXML
    private TableView summaryTable;
    @FXML
    private TableColumn<CandidateTable, String> candidateColumn;
    @FXML
    private TableColumn<CandidateTable, Integer> voteColumn;
    @FXML
    private TableColumn<CandidateTable, String> partyColumn;
    @FXML
    private Tab statTab;
    @FXML
    private Tab chartTab;
    @FXML
    private TableView partyTable;
    @FXML
    private TableColumn partyNamesColumn;
    @FXML
    private TableColumn partyVotesColumn;
    @FXML
    private Label voidedVotesLabel;
    @FXML
    private Label disallowedVotesLabel;

    private int voidedVotes = 0;
    private int disallowedVotes = 0;

    private final ObservableList<CandidateTable> candidatesData = FXCollections.observableArrayList();
    private final ObservableList<PartyTable> partiesData = FXCollections.observableArrayList();
    private MultiValueMap<String, Candidate> allCandidatesMap;


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

        refreshAll();
    }

    @FXML
    public void refresh() {
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

    @FXML
    private void logout() {
        lo();
    }

    private void lo() {
        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
        VoteCalculatorApplication.stage.setWidth(loginWindow.getWidth());
        VoteCalculatorApplication.stage.setHeight(loginWindow.getHeight());
        VoteCalculatorApplication.stage.setScene(loginWindow);
        SecurityContextHolder.clearContext();
    }

    @FXML
    private void exportPDF() {

        candidateChart.setAnimated(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("summary.pdf");
        fileChooser.setTitle("Save summary in pdf");
        File file = fileChooser.showSaveDialog(VoteCalculatorApplication.stage);
        if (file != null) {
            try {

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));

                document.open();
                Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
                Font summaryFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                Font tipFont = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.DARK_GRAY);

                WritableImage img = new WritableImage((int) candidateChart.getWidth(), (int) candidateChart.getHeight());
                SnapshotParameters params = new SnapshotParameters();
                WritableImage snapshot = candidateChart.snapshot(params, img);
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", byteOutput);

                Image image = com.itextpdf.text.Image.getInstance(byteOutput.toByteArray());
                float width = PageSize.A4.getWidth() * 0.85f;
                image.scaleAbsoluteWidth(width);
                float height = image.getHeight() * 0.85f;
                image.scaleToFit(width, height);


                WritableImage imgParty = new WritableImage((int) partyChart.getWidth(), (int) partyChart.getHeight());
                SnapshotParameters paramsParty = new SnapshotParameters();
                WritableImage snapshotParty = partyChart.snapshot(paramsParty, imgParty);
                ByteArrayOutputStream byteOutputParty = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(snapshotParty, null), "png", byteOutputParty);

                Image imageParty = com.itextpdf.text.Image.getInstance(byteOutputParty.toByteArray());

                PdfPTable candidateTable = new PdfPTable(3);

                Stream.of("Name", "Party", "Votes").forEach(c -> {
                    PdfPCell h = new PdfPCell();
                    h.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    h.setBorderWidth(2);
                    h.setPhrase(new Phrase(c));
                    candidateTable.addCell(h);
                });

                for (CandidateTable c : candidatesData) {
                    candidateTable.addCell(c.getName());
                    candidateTable.addCell(c.getParty());
                    candidateTable.addCell(String.valueOf(c.getVotes()));
                }

                PdfPTable partyTable = new PdfPTable(2);

                Stream.of("Party", "Votes").forEach(c -> {
                    PdfPCell h = new PdfPCell();
                    h.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    h.setBorderWidth(2);
                    h.setPhrase(new Phrase(c));
                    partyTable.addCell(h);
                });

                for (PartyTable p : partiesData) {
                    partyTable.addCell(p.getName());
                    partyTable.addCell(String.valueOf(p.getVotes()));
                }

                PdfPTable voidedVotes = new PdfPTable(2);

                voidedVotes.addCell("*Voided votes");
                voidedVotes.addCell(String.valueOf(this.voidedVotes));
                voidedVotes.addCell("Disallowed votes");
                voidedVotes.addCell(String.valueOf(this.disallowedVotes));

                candidateTable.setSpacingAfter(10);
                partyTable.setSpacingAfter(10);
                voidedVotes.setSpacingAfter(0);

                candidateTable.setSpacingBefore(10);
                partyTable.setSpacingBefore(10);
                voidedVotes.setSpacingBefore(10);

                document.add(new Paragraph("Vote summary", headerFont));
                document.add(new Paragraph("Candidates summary", summaryFont));
                document.add(candidateTable);
                document.add(new Paragraph("Parties summary", summaryFont));
                document.add(partyTable);
                document.add(new Paragraph("Voided votes summary", summaryFont));
                document.add(voidedVotes);
                document.add(new Chunk("*Disallowed votes included in voided votes", tipFont));
                document.add(Chunk.NEXTPAGE);
                document.add(new Paragraph("Charts", headerFont));
                document.add(new Paragraph("Candidates chart", summaryFont));
                document.add(image);
                document.add(new Paragraph("Parties chart", summaryFont));
                document.add(imageParty);
                document.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                candidateChart.setAnimated(true);
            }
        }
    }

    @FXML
    private void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("summary.csv");
        fileChooser.setTitle("Export summary to csv");
        File file = fileChooser.showSaveDialog(VoteCalculatorApplication.stage);

        if (file != null) {
            try {
                FileWriter writer = null;
                writer = new FileWriter(file);

                CSVUtils.writeLine(writer, Arrays.asList("name", "party", "votes"));

                for (CandidateTable c : candidatesData)
                    CSVUtils.writeLine(writer, Arrays.asList(c.getName(), c.getParty(), String.valueOf(c.getVotes())));

                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setCandidateData() {
        allCandidatesMap = candidateService.getAllCandidatesToMap();
        List<PartyTable> partiesList = new ArrayList<>();
        List<CandidateTable> candidateList = new ArrayList<>();

        Set<String> strings = allCandidatesMap.keySet();
        for (String key : strings) {
            int partyVotes = 0;
            List<Candidate> candidates = allCandidatesMap.get(key);
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

    private void setCandidateChart() {
        candidateChart.getData().clear();

        Set<String> strings = allCandidatesMap.keySet();
        for (String k : strings) {
            List<Candidate> candidates = allCandidatesMap.get(k);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(k);
            for (Candidate c : candidates) {
                series1.getData().add(new XYChart.Data(c.getName(), c.getVotes()));
            }
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

    @FXML
    private void refreshMenu() {
        refreshAll();
    }

    @FXML
    private void logoutMenu() {
        lo();
    }

    @FXML
    private void close() {
        System.exit(0);
    }

    private void setPartyChart() {
        partyChart.getData().clear();

        for (PartyTable pt : this.partiesData) {
            XYChart.Series series = new XYChart.Series();
            series.setName(pt.getName());
            series.getData().add(new XYChart.Data(pt.getName(), pt.getVotes()));
            partyChart.getData().add(series);
        }
    }
}
