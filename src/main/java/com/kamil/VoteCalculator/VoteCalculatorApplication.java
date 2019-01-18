package com.kamil.VoteCalculator;

import com.kamil.VoteCalculator.model.candidate.CandidateService;
import com.kamil.VoteCalculator.model.party.PartyService;
import com.kamil.VoteCalculator.model.role.RolesService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class VoteCalculatorApplication extends Application {

    private final Logger logger = LoggerFactory.getLogger(VoteCalculatorApplication.class);

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
}

