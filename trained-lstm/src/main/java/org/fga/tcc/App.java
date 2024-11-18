package org.fga.tcc;

import org.fga.tcc.facades.VotingModelFacade;

public class App {

    public static void main( String[] args ) {
        VotingModelFacade votingModelFacade = VotingModelFacade.getInstance();
        String parentDirectory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";

        votingModelFacade.generateTrainedModel(parentDirectory);
    }

}
