package com.kamil.VoteCalculator.model.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {

    @Autowired
    CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates(){
        return candidateRepository.findAll();
    }

    public List<Candidate> saveAllCandidates(List<Candidate> candidates){
        List<Candidate> candidateList = candidateRepository.saveAll(candidates);
        return candidateList;
    }

    public Candidate saveCandidate(Candidate candidate){
        Candidate save = candidateRepository.save(candidate);
        return save;
    }

}
