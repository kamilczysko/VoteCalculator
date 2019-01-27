package com.kamil.VoteCalculator.model.party;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PartyTable {

	SimpleStringProperty name = new SimpleStringProperty();
	SimpleIntegerProperty votes = new SimpleIntegerProperty();

	public PartyTable(String name, int votes) {
		setName(name);
		setVotes(votes);
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
