package com.kamil.VoteCalculator.model;

public class Person {

    private String pesel;

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    @Override
    public String toString() {
        return "ClassPojo [pesel = " + pesel + "]";
    }
}

