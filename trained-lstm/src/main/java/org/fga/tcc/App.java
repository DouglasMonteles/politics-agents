package org.fga.tcc;

import org.fga.tcc.services.VotingModelService;

public class App {

    public static void main( String[] args ) {
        VotingModelService votingModelService = VotingModelService.getInstance();

        votingModelService
            .setModelPath("")
            .prepareWordVector()
            .trainModel();
    }

}
