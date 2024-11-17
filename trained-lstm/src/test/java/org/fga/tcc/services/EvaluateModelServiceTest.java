package org.fga.tcc.services;

import org.fga.tcc.services.EvaluateModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EvaluateModelServiceTest {

    private final EvaluateModelService evaluateModelService;

    public EvaluateModelServiceTest() {
        String pathModel = "/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalKeywords/PSD";
        this.evaluateModelService = new EvaluateModelService(pathModel);
    }

    @Test
    public void predictAgainstVote() {
        String proposal = "Criação, Estratégia Nacional de Formação de Especialistas para a Saúde";
        String expectedResult = "against";
        String actualResult = this.evaluateModelService.evaluateVoteModel(proposal);

        Assertions.assertEquals(expectedResult, actualResult);
    }

}
