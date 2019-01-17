package com.kamil.VoteCalculator.model.candidate;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.kamil.VoteCalculator.model.party.Party;

import javax.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JacksonXmlProperty(isAttribute = false)
    long id;

    @Column(nullable = false)
    String name;

    @JoinColumn(nullable = false)
    @OneToOne
    Party party = new Party();

    @JacksonXmlProperty(isAttribute = false)
    int votes = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Party getParty() {
        return party;
    }

    public String getPartyName() {
        return party.getParty();
    }

    public void setParty(String party) {
        this.party.setParty(party);
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void increaseVote() {
        votes++;
    }

    @Override
    public String toString() {
        return id + " - " + getName() + " - " + getParty();
    }
}
