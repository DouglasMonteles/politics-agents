package org.fga.tcc.services;

import org.fga.tcc.entities.*;

import java.util.List;
import java.util.Set;

public interface VoteService {

    List<Voting> getVotingByYear(int year);

    List<NominalVote> getVotesByVotingId(String votingId);

    List<VotingObject> getVotingObjectByYear(Integer year);

    void savePureData();

    Voting getVotingById(String votingId);

    List<String> getAllVotingIdsWithNominalVotes();

    List<VotingOrientation> getOrientationAboutTheVoting(String votingId);

    Set<String> getAllVotesIds();

    void generateDataAboutPartyOrientation();

    void generateDataAboutPartyProposalKeywords();

}
