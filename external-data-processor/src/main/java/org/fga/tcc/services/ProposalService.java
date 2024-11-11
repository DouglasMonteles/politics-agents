package org.fga.tcc.services;

import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.entities.Proposal;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.entities.VotingObject;

import java.util.List;

public interface ProposalService {


    List<Proposal> getProposalByYear(int year);

    Proposal getProposalById(String id);

    List<Voting> getVotingOfProposalById(String id);


}
