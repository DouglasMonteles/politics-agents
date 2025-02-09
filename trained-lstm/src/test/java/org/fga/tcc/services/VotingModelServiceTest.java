package org.fga.tcc.services;

import org.fga.tcc.exceptions.ValidationException;
import org.fga.tcc.services.impl.VotingModelServiceImpl;
import org.junit.jupiter.api.*;
import org.nd4j.linalg.exception.ND4JIllegalStateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Disabled
public class VotingModelServiceTest {

    private static final String MODEL_PATH = System.getProperty("user.dir") + "/src/test/resources/data/keywords";

    private final VotingModelService evaluateModelService;

    public VotingModelServiceTest() {
        this.evaluateModelService = new VotingModelServiceImpl();
        this.evaluateModelService.setModelPath(MODEL_PATH);
    }

    @BeforeAll
    public static void beforeAll() {
        clearData();
    }

    @AfterEach
    public void afterEach() {
        clearData();
    }

    @Test
    public void generateWordVector() {
        this.evaluateModelService.prepareWordVector();
        String wordVectorName = new File(MODEL_PATH, VotingModelService.WORD_VECTOR_NAME).getName();

        Assertions.assertEquals(wordVectorName, "WordVector.txt");
    }

    @Test
    public void generateTrainedModel() {
        this.evaluateModelService.prepareWordVector();
        this.evaluateModelService.trainModel();
        String modelName = new File(MODEL_PATH, VotingModelService.MODEL_NAME).getName();

        Assertions.assertEquals(modelName, "VotesModel.net");
    }

    @Test
    public void predictAgainstVote() {
        String proposalKeywords = "Alteração, Lei dos Registros Públicos, funcionamento, serviços, registro público.";
        String expectedResult = "against";

        this.evaluateModelService.prepareWordVector();
        this.evaluateModelService.trainModel();
        String actualResult = this.evaluateModelService.evaluateVoteModel(proposalKeywords);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void predictFavorVote() {
        String proposalKeywords = "Definição, biometano, produtor, acesso, gasoduto de transporte.";
        String expectedResult = "favor";

        this.evaluateModelService.prepareWordVector();
        this.evaluateModelService.trainModel();
        String actualResult = this.evaluateModelService.evaluateVoteModel(proposalKeywords);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void throwsExceptionWhenModelPathIsNotInformedAndGenerateWordVectorIsCalled() {
        String expectedErrorMessage = "Attribute [modelPath] can't be null or empty.";

        String[] invalidValues = {
            "",
            null,
        };

        List<Runnable> processToGenerateModel = new ArrayList<>();
        processToGenerateModel.add(this.evaluateModelService::prepareWordVector);
        processToGenerateModel.add(this.evaluateModelService::trainModel);
        processToGenerateModel.add(() -> this.evaluateModelService.evaluateVoteModel(null));

        for (String invalidValue : invalidValues) {
            for (Runnable func : processToGenerateModel) {
                Exception e = Assertions.assertThrows(ValidationException.class, () -> {
                    this.evaluateModelService.setModelPath(invalidValue);
                    func.run();
                });
                Assertions.assertEquals(expectedErrorMessage, e.getMessage());
            }
        }
    }

    @Test
    public void throwsExceptionWhenTrainModelIsCalledWithoutWordVector() {
        String expectedErrorMessage = "File [" + System.getProperty("user.dir") + "/src/test/resources/data/keywords/" + VotingModelService.WORD_VECTOR_NAME + "] doesn't exist";
        Exception e = Assertions.assertThrows(ND4JIllegalStateException.class, this.evaluateModelService::trainModel);

        Assertions.assertEquals(expectedErrorMessage, e.getMessage());
    }

    private static void clearData() {
        String wordVectorPath = MODEL_PATH + File.separator + VotingModelService.WORD_VECTOR_NAME;
        File wordVectorFile = new File(wordVectorPath);

        String modelPath = MODEL_PATH + File.separator + VotingModelService.MODEL_NAME;
        File modelFile = new File(modelPath);

        if (wordVectorFile.exists()) {
            wordVectorFile.delete();
        }

        if (modelFile.exists()) {
            modelFile.delete();
        }
    }

}
