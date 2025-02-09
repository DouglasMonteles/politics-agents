package org.fga.tcc;

import org.fga.tcc.facades.PrepareDatabaseFacade;

public class App {
    public static void main( String[] args ) {
        // TODO: talvez adiconar um agente para isso
        PrepareDatabaseFacade.prepareDatabaseForTraining();
    }
}
