package com.kamil.VoteCalculator.model;

public class Disallowed {
    private Person[] person;

    private String publicationDate;

    public Person[] getPerson() {
        return person;
    }

    public void setPerson(Person[] person) {
        this.person = person;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public boolean isDisallowed(String pesel){
        for(Person p : getPerson()){
            if(p.getPesel().equals(pesel))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ClassPojo [person = " + person + ", publicationDate = " + publicationDate + "]";
    }
}