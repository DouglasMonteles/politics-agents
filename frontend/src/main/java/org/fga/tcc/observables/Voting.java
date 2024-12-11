package org.fga.tcc.observables;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Voting extends Subject {

    private static final Voting INSTANCE = new Voting();

    @Getter
    Map<Integer, Integer> votes = new HashMap<>();

    public void setVotes(Integer deputyId, Integer vote) {
        this.votes.put(deputyId, vote);
        notifyObservers();
    }

    public static Voting getInstance() {
        return INSTANCE;
    }

}
