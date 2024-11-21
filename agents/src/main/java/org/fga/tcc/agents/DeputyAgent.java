package org.fga.tcc.agents;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.*;
import org.fga.tcc.exceptions.AgentException;
import org.fga.tcc.ontologies.DeputyOntology;
import org.fga.tcc.ontologies.concept.ProposalConcept;
import org.fga.tcc.ontologies.predicate.AnalysisProposalPredicate;
import org.fga.tcc.ontologies.predicate.ApprovedProposalPredicate;
import org.fga.tcc.ontologies.predicate.RejectedProposalPredicate;
import org.fga.tcc.services.VotingModelService;
import org.fga.tcc.services.impl.VotingModelServiceImpl;

import java.io.Serial;

@Getter
@EqualsAndHashCode(of = {"deputyName", "partyAcronym"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DeputyAgent extends Agent {

    private final SLCodec codec = new SLCodec();
    private final Ontology ontology = DeputyOntology.getInstance();

    @Serial
    private static final long serialVersionUID = -7061619046169059737L;

    private String deputyName;
    private String partyAcronym;
    private String proposalToAnalysis;
    @Setter
    private char vote;

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        Object[] args = getArguments();

        if (args == null || args.length < 2) {
            throw new AgentException("Args is null or its length is not enough.");
        }

        this.deputyName = (String) args[0];
        this.partyAcronym = (String) args[1];

        System.out.println("Deputy " + getLocalName() + " is ready");
        System.out.println("Nome: " + deputyName);
        System.out.println("Partido: " + partyAcronym);

        addBehaviour(new VotingBehaviour(this));
    }

    private class VotingBehaviour extends CyclicBehaviour {

        @Serial
        private static final long serialVersionUID = 6853932082308453796L;

        private final DeputyAgent deputyAgent;

        public VotingBehaviour(DeputyAgent deputyAgent) {
            this.deputyAgent = deputyAgent;
        }

        @Override
        public void action() {
            ACLMessage requestMessage = receive();

            if (requestMessage != null) {
                try {
                    Object content = getContentManager().extractContent(requestMessage);

                    if (content instanceof AnalysisProposalPredicate analysisProposal) {
                        VotingModelService votingModelService = VotingModelServiceImpl.getInstance();
                        ProposalConcept proposal = analysisProposal.getProposal();

                        // TODO: Analyze proposal
                        String result = votingModelService
                            .setModelPath("/home/douglas/Documentos/www/politics-agents/trained-data/votes/partyOrientation/proposalDescription/PT")
                            .evaluateVoteModel(proposal.getDescription());

                        ACLMessage message = requestMessage.createReply();
                        message.setPerformative(ACLMessage.INFORM);
                        message.setLanguage(codec.getName());
                        message.setOntology(ontology.getName());

                        if (result.equalsIgnoreCase("favor")) {
                            ApprovedProposalPredicate approvedProposal = new ApprovedProposalPredicate();
                            approvedProposal.setProposal(proposal);
                            getContentManager().fillContent(message, approvedProposal);
                        } else {
                            RejectedProposalPredicate rejectedProposal = new RejectedProposalPredicate();
                            rejectedProposal.setProposal(proposal);
                            getContentManager().fillContent(message, rejectedProposal);
                        }

                        send(message);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

}
