package org.fga.tcc.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.pages.HomePage;
import org.fga.tcc.pages.VotingResultComparisonPage;
import org.fga.tcc.pages.VotingResultPage;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.VotingService;
import org.fga.tcc.services.impl.DeputyServiceImpl;
import org.fga.tcc.services.impl.VotingServiceImpl;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class FrontendAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -2797189365541352024L;

    private final HomePage homePage = new HomePage();

    private final DeputyService deputyService = DeputyServiceImpl.getInstance();
    private final VotingService voteService = VotingServiceImpl.getInstance();

    @Override
    protected void setup() {
        homePage
            .setButtonHandleInfo((data, proposal) -> {
                try {
                    System.out.println("Sending list of agents ids: " + data);
                    System.out.println("Proposal text: " + proposal);

                    ACLMessage requestMsg = new ACLMessage(ACLMessage.PROPOSE);
                    requestMsg.addReceiver(getAID("DeputyEnvironmentAgent"));
                    requestMsg.setContentObject(proposal);

                    send(requestMsg);

                    List<Deputy> deputies = deputyService.getDeputes()
                        .stream()
                        .filter(deputy -> data.contains(deputy.getName()) || data.contains(deputy.getPartyAcronym()))
                        .toList();

                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                    message.addReceiver(getAID("DeputyEnvironmentAgent"));
                    message.setContentObject(new ArrayList<>(deputies));

                    send(message);

                    new VotingResultPage()
                            .setDeputies(deputies)
                            .buildPage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .setButtonVoteSimulation((data, proposal) -> {
                try {
                    System.out.println("Process vote data: " + data);

                    String votingId = (String) data.getFirst();

                    Voting voting = voteService.getVotingById(votingId);
                    List<NominalVote> nominalVotes = voteService.getVotesByVotingId(votingId);

                    ACLMessage requestMsg = new ACLMessage(ACLMessage.PROPOSE);
                    requestMsg.addReceiver(getAID("DeputyEnvironmentAgent"));
                    requestMsg.setContentObject(voting.getProposal().getDescription());

                    send(requestMsg);

                    List<Deputy> deputies = nominalVotes
                            .stream()
                            .map(NominalVote::getDeputy)
                            .toList();

                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.addReceiver(getAID("DeputyEnvironmentAgent"));
                    message.setContentObject(new ArrayList<>(deputies));

                    send(message);

                    new VotingResultComparisonPage()
                            .setProposal(voting.getProposal().getDescription())
                            .setNominalVotes(nominalVotes)
                            .buildPage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .buildPage();
    }

}
