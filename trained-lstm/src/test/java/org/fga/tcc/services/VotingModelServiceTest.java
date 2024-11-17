package org.fga.tcc.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VotingModelServiceTest {

    private final VotingModelService evaluateModelService;

    public VotingModelServiceTest() {
        this.evaluateModelService = new VotingModelService();
    }

    @Test
    public void predictAgainstVote() {
        String pathModel = "/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalKeywords/PSD";
        String proposal = "Criação, Estratégia Nacional de Formação de Especialistas para a Saúde";
        String expectedResult = "against";
        String actualResult = this.evaluateModelService.evaluateVoteModel(pathModel, proposal);

        Assertions.assertEquals(expectedResult, actualResult);
    }

}
