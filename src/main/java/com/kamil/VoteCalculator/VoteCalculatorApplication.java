package com.kamil.VoteCalculator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kamil.VoteCalculator.gui.MainWindow;
import com.kamil.VoteCalculator.model.Disallowed;
import com.kamil.VoteCalculator.model.Person;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));
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


    RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
    }
}

