package com.kamil.VoteCalculator.gui;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Statistics {

    @Autowired
    ApplicationContext context;

    @FXML
    private void logout() {
        Scene loginWindow = context.getBean("loadLoginWindow", Scene.class);
        VoteCalculatorApplication.stage.setScene(loginWindow);
        SecurityContextHolder.clearContext();
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

}
