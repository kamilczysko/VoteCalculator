package com.kamil.VoteCalculator.configuration;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class GUIConfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    @Scope("prototype")
    public Scene loadLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VoteCalculatorApplication.class.getResource("/gui/LoginWindow.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @Scope("prototype")
    public Scene loadVoteWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VoteCalculatorApplication.class.getResource("/gui/VoteList.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @Scope("prototype")
    public Scene loadStatisticsWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VoteCalculatorApplication.class.getResource("/gui/Statistics.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
