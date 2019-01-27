package com.kamil.VoteCalculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication public class VoteCalculatorApplication extends Application {

	private static final Logger logger = LoggerFactory.getLogger(VoteCalculatorApplication.class);

	private ConfigurableApplicationContext context;
	public static Stage stage;
	public static boolean firstRun = false;

	public static void main(String[] args) {
		if (args.length > 0) {
			logger.debug("ARGS: [{}]", args[0]);
			firstRun = true;
		}
		Application.launch(args);
	}

	@Override public void init() throws Exception {
		context = SpringApplication.run(VoteCalculatorApplication.class);
	}

	@Override public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Vote App");
		Scene loadLoginWindow = context.getBean("loadLoginWindow", Scene.class);
		primaryStage.setResizable(true);
		primaryStage.setScene(loadLoginWindow);
		primaryStage.sizeToScene();
		primaryStage.show();
		this.stage = primaryStage;
	}

	@Override public void stop() throws Exception {
		context.stop();
	}
}

