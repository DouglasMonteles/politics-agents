package org.fga.tcc.facades;

import org.fga.tcc.services.VotingModelService;
import org.fga.tcc.utils.LstmFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VotingModelFacade {

    private static Logger LOG = LoggerFactory.getLogger(VotingModelFacade.class);

    private static final VotingModelFacade INSTANCE = new VotingModelFacade();

    private final VotingModelService votingModelService;

    public VotingModelFacade() {
        this.votingModelService = VotingModelService.getInstance();
    }

    public void generateTrainedModel(String parentDirectory) {
        // TODO: considerar somente os que tiverem 0.txt e 1.txt ou não gerar diretórios sem os arquivos com as classes
        List<String> excludedDirectories = new ArrayList<>();
        excludedDirectories.add("/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalKeywords/PRD");
        excludedDirectories.add("/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalDescription/PRD");

        LstmFileUtils.iterateDirectory(parentDirectory, (partyAcronymChildDirectory) -> {
            String path = partyAcronymChildDirectory.toAbsolutePath().toString();
            System.out.println("Path: " + path);
            LOG.info("Path: {}", path);

            if (!excludedDirectories.contains(path)) {
                this.votingModelService
                    .setModelPath(path)
                    .prepareWordVector()
                    .trainModel();
            }
        });
    }

    public static void generateModelAboutPartyVotingOrientation() {
        VotingModelFacade votingModelFacade = VotingModelFacade.getInstance();

        // Generate trained models
        String proposalDescriptionParentDirectory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";
        votingModelFacade.generateTrainedModel(proposalDescriptionParentDirectory);
    }

    public static VotingModelFacade getInstance() {
        return INSTANCE;
    }

}
