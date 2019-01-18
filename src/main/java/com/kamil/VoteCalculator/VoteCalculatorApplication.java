package com.kamil.VoteCalculator;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kamil.VoteCalculator.model.candidate.Candidate;
import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.candidate.Candidates;
import com.kamil.VoteCalculator.model.party.Party;
import com.kamil.VoteCalculator.model.party.PartyService;
import com.kamil.VoteCalculator.model.role.Roles;
import com.kamil.VoteCalculator.model.role.RolesService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class VoteCalculatorApplication extends Application implements CommandLineRunner {

    private ConfigurableApplicationContext context;
    private Parent root;
    public static Stage stage;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CandidateService candidateService;
    @Autowired
    PartyService partyService;
    @Autowired
    RolesService rolesService;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        context = SpringApplication.run(VoteCalculatorApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Vote App");
        Scene loadLoginWindow = context.getBean("loadLoginWindow", Scene.class);
        primaryStage.setResizable(true);
        primaryStage.setScene(loadLoginWindow);
        primaryStage.sizeToScene();
        primaryStage.show();
        this.stage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        context.stop();
    }

    @Override
    public void run(String... args) throws Exception {
        initCandidates();

        Roles unvoted = new Roles();
        unvoted.setUserRole("unvoted");
        Roles voted = new Roles();
        voted.setUserRole("voted");
        List<Roles> roles = rolesService.saveRoles(Arrays.asList(voted, unvoted));
    }

    private void initCandidates() throws IOException {
        Candidates candidates = getCandidatesFromServer();
        Set<Party> partySet = candidates.getAllParties();
        List<Party> partiesList = new ArrayList<>();
        partiesList.addAll(partySet);
        List<Party> partiesSaved = partyService.saveParties(partiesList);

        Map<String, Party> map = new HashMap<String, Party>();
        for (Party p : partiesSaved) map.put(p.getParty(), p);

        List<Candidate> candidatesList = candidates.getAllCandidates();
        for (Candidate c : candidatesList) {
            Party party = map.get(c.getPartyName());
            c.setParty(party);
        }
        List<Candidate> candidatesSaved = candidateService.saveAllCandidates(candidatesList);
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
}

