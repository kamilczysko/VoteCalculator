package com.kamil.VoteCalculator.model.candidate;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kamil.VoteCalculator.VoteCalculatorApplication;
import com.kamil.VoteCalculator.model.party.Party;
import com.kamil.VoteCalculator.model.party.PartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CandidateService implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    @Autowired
    private PartyService partyService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public MultiValueMap<String, Candidate> getAllCandidatesToMap() {
        List<Candidate> all = candidateRepository.findAll();
        MultiValueMap<String, Candidate> map = new LinkedMultiValueMap<>();

        for (Candidate c : all)
            map.add(c.getPartyName(), c);
        return map;
    }

    public List<Candidate> saveAllCandidates(List<Candidate> candidates) {
        List<Candidate> candidateList = candidateRepository.saveAll(candidates);
        return candidateList;
    }

    public Candidate saveCandidate(Candidate candidate) {
        Candidate save = candidateRepository.save(candidate);
        return save;
    }

    @Secured("ROLE_unvoted")
    public void vote(Candidate candidate, boolean badVote) {
        candidate.increaseVote();
        candidateRepository.save(candidate);
    }

    @Override
    public void run(String... args) throws Exception {
        if (VoteCalculatorApplication.firstRun) {
            logger.info("CANDIDATES INIT");
            initCandidatesInDB();
        }
    }

    private void initCandidatesInDB() throws IOException {
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

        saveAllCandidates(candidatesList);
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
