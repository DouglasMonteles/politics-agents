package org.fga.tcc;

import org.fga.tcc.facades.VotingModelFacade;

public class App {

    public static void main( String[] args ) {
        VotingModelFacade votingModelFacade = VotingModelFacade.getInstance();

        // Generate trained models
        String proposalDescriptionParentDirectory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";
        votingModelFacade.generateTrainedModel(proposalDescriptionParentDirectory);

        String proposalKeywordsParentDirectory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalKeywords";
        votingModelFacade.generateTrainedModel(proposalKeywordsParentDirectory);
        // -- END --
    }

}
