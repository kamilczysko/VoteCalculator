package com.kamil.VoteCalculator.model.party;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PartyService {

    @Autowired
    PartyRepository partyRepository;

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public List<Party> saveParties(List<Party> parties) {
        List<Party> savedParties = partyRepository.saveAll(parties);
        return savedParties;
    }
}
