package com.kamil.VoteCalculator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kamil.VoteCalculator.gui.MainWindow;
import com.kamil.VoteCalculator.model.Disallowed;
import com.kamil.VoteCalculator.model.Person;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.candidate.Candidates;
import com.kamil.VoteCalculator.model.party.Party;
import com.kamil.VoteCalculator.model.party.PartyService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class VoteCalculatorApplication extends Application implements CommandLineRunner {

    private ConfigurableApplicationContext contet;
    private Parent root;
    MainWindow mainWindowController;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {

        contet = SpringApplication.run(VoteCalculatorApplication.class);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/VoteList.fxml"));
        loader.setControllerFactory(contet::getBean);
        mainWindowController = loader.getController();
        root = loader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        primaryStage.setTitle("Vote App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        contet.stop();
    }

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CandidateService candidateService;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    PartyService partyService;


    @Override
    public void run(String... args) throws Exception {
        initCandidates();
    }

    private void initCandidates() throws IOException {
        Candidates candidates = getCandidatesFromServer();
        Set<Party> partySet = candidates.getAllParties();
        List<Party> partiesList = new ArrayList<>();
        partiesList.addAll(partySet);
        List<Party> partiesSaved = partyService.saveParties(partiesList);

        Map<String, Party> map = new HashMap<String, Party>();
        for (Party p : partiesSaved) map.put(p.getParty(), p);

        System.out.println(map);
        List<Candidate> candidatesList = candidates.getAllCandidates();
        for (Candidate c : candidatesList) {
            Party party = map.get(c.getPartyName());
            c.setParty(party);
        }
        List<Candidate> candidatesSaved = candidateService.saveAllCandidates(candidatesList);

        System.out.println(candidatesSaved);
        System.out.println(partiesSaved);
    }

    private Candidates getCandidatesFromServer() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://webtask.future-processing.com:8069/candidates", HttpMethod.GET, entity, String.class);

        Pattern p = Pattern.compile("(" + Pattern.quote("<candidates>") + "(.*?)" + Pattern.quote("</candidates>") + ")");
        Matcher m = p.matcher(response.toString());
        String result = "";
        while (m.find())
            result = m.group(1);

        System.out.println(result);

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        XmlMapper mapper = new XmlMapper(module);
        Candidates candidates = mapper.readValue(result, Candidates.class);

        return candidates;
    }

    public void getDisallowed() throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://webtask.future-processing.com:8069/blocked", HttpMethod.GET, entity, String.class);

        Pattern p = Pattern.compile("(" + Pattern.quote("<disallowed>") + "(.*?)" + Pattern.quote("</disallowed>") + ")");
        Matcher m = p.matcher(response.toString());
        String result = "";
        while (m.find())
            result = m.group(1);

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        XmlMapper mapper = new XmlMapper(module);
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        Disallowed disallowed = mapper.readValue(result, Disallowed.class);
        for (Person per : disallowed.getPerson()) {
            System.out.println(per);
        }
    }
}

