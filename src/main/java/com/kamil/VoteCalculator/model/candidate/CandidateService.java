package com.kamil.VoteCalculator.model.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class CandidateService {

    @Autowired
    CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public MultiValueMap<String, Candidate> getAllCandidatesToMap() {
        List<Candidate> all = candidateRepository.findAll();
        MultiValueMap<String, Candidate> map = new LinkedMultiValueMap<>();

        for(Candidate c : all)
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
        candidate.increaseVote(badVote);
        candidateRepository.save(candidate);
    }

}
