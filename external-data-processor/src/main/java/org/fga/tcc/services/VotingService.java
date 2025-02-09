package org.fga.tcc.services;

import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.entities.VotingObject;
import org.fga.tcc.entities.VotingOrientation;

import java.util.List;
import java.util.Set;

public interface VotingService {

    List<Voting> getVotingByYear(int year);

    List<NominalVote> getVotesByVotingId(String votingId);

    List<VotingObject> getVotingObjectByYear(Integer year);

    Voting getVotingById(String votingId);

    List<VotingOrientation> getOrientationAboutTheVoting(String votingId);

    Set<String> getAllVotesIds();

    void generateFavorAndAgainstFilesWithProposalResume();

}
