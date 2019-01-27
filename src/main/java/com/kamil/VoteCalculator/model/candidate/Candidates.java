package com.kamil.VoteCalculator.model.candidate;

import com.kamil.VoteCalculator.model.party.Party;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Candidates {
	private Candidate[] candidate;

	private String publicationDate;

	public Candidate[] getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate[] candidate) {
		this.candidate = candidate;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public List<Candidate> getAllCandidates() {
		return Arrays.asList(getCandidate());
	}

	public Set<Party> getAllParties() {
		Set<Party> ps = new HashSet<>();
		for (Candidate c : getCandidate()) {
			ps.add(c.getParty());
		}
		return ps;
	}

	@Override public String toString() {
		return "ClassPojo [candidate = " + candidate + ", publicationDate = " + publicationDate + "]";
	}
}