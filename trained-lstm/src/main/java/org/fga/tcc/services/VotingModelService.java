package org.fga.tcc.services;

import org.fga.tcc.services.impl.VotingModelServiceImpl;

public interface VotingModelService {

    String MODEL_NAME = "VotesModel.net";
    String WORD_VECTOR_NAME = "WordVector.txt";
    String PURE_TXT_NAME = "ProposalWordVector.txt";

    VotingModelService setModelPath(String modelPath);

    VotingModelService prepareWordVector();

    VotingModelService trainModel();

    String evaluateVoteModel(String proposal);

    static VotingModelService getInstance() {
        return VotingModelServiceImpl.getInstance();
    }

}
