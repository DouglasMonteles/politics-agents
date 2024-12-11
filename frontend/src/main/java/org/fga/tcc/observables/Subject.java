package org.fga.tcc.observables;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {

    List<VotingObserver> observers = new ArrayList<>();

    public void addObserver(VotingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(VotingObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (VotingObserver observer : observers) {
            observer.update();
        }
    }

}
