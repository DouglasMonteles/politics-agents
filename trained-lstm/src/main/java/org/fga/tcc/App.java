package org.fga.tcc;

import org.fga.tcc.services.VotingModelService;
import org.fga.tcc.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main( String[] args ) {
        VotingModelService votingModelService = VotingModelService.getInstance();

        String parentDirectory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";

        // TODO: considerar somente os que tiverem 0.txt e 1.txt
        List<String> excludedDirectories = new ArrayList<>();
        excludedDirectories.add("/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalKeywords/PRD");
        excludedDirectories.add("/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalDescription/PRD");

        FileUtils.iterateDirectory(parentDirectory, (partyAcronymChildDirectory) -> {
            String path = partyAcronymChildDirectory.toAbsolutePath().toString();

            if (!excludedDirectories.contains(path)) {
                votingModelService
                    .setModelPath(path)
                    .prepareWordVector()
                    .trainModel();
            }
        });
    }

}
