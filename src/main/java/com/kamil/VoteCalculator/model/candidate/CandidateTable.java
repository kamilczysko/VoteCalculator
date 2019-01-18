package com.kamil.VoteCalculator.model.candidate;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CandidateTable {

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty party = new SimpleStringProperty();
    private SimpleIntegerProperty votes = new SimpleIntegerProperty();

    public CandidateTable(Candidate candidate) {
//        System.out.println("$$$$"+candidate.getName());
        setName(candidate.getName());
        setParty(candidate.getPartyName());
        setVotes(candidate.getVotes());
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getParty() {
        return party.get();
    }

    public SimpleStringProperty partyProperty() {
        return party;
    }

    public void setParty(String party) {
        this.party.set(party);
    }

    public int getVotes() {
        return votes.get();
    }

    public SimpleIntegerProperty votesProperty() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes.set(votes);
    }
}
