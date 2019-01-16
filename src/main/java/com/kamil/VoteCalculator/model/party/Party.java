package com.kamil.VoteCalculator.model.party;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.*;

@Entity
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = "party_name")})
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, name = "party_name")
    @JacksonXmlProperty(namespace = "party", localName = "party")
    String partyName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParty() {
        return partyName;
    }

    public void setParty(String party) {
        this.partyName = party;
    }

    @Override
    public String toString() {
        return id+"-"+getParty();
    }

    @Override
    public int hashCode() {
        return partyName.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return partyName.equals(((Party)obj).getParty());
    }


}
