package org.fga.tcc;

import org.fga.tcc.facades.VotingModelFacade;

public class App {

    public static void main( String[] args ) {
        // TODO: adicionar agente para treino
        VotingModelFacade.generateModelAboutPartyVotingOrientation();
    }

}
