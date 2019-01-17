package com.kamil.VoteCalculator.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.gui.VoteList;
import com.kamil.VoteCalculator.model.Disallowed;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Disallowed getDisallowed() throws IOException {

        RestTemplate restTemplate = restTemplate();

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

        return disallowed;
    }


//    @Bean
//    @Scope("prototype")
//    public VoteList voteController(){
//        return new VoteList();
//    }
//
//    @Bean
//    @Scope("prototype")
//    public Stage loadVoteWindow() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(VoteCalculatorApplication.class.getResource("/gui/VoteList.fxml"));
//            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//            Stage stage = new Stage();
//            stage.setTitle("New Window");
//            stage.setScene(scene);
//            return stage;
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return null;
//    }

}
