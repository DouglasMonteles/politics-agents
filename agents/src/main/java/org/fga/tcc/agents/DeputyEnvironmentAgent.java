package org.fga.tcc.agents;

import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.fga.tcc.ontologies.DeputyOntology;
import org.fga.tcc.ontologies.concept.ProposalConcept;
import org.fga.tcc.ontologies.predicate.AnalysisProposalPredicate;
import org.fga.tcc.ontologies.predicate.ApprovedProposalPredicate;
import org.fga.tcc.ontologies.predicate.RejectedProposalPredicate;

import java.io.Serial;

public class DeputyEnvironmentAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -1951592876859134303L;

    private final SLCodec codec = new SLCodec();
    private final Ontology ontology = DeputyOntology.getInstance();

    private String conversationId;

    @Override
    protected void setup() {
        // FIPA-SL
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        addBehaviour(new SendProposalToAnalysisBehaviour(this, 5000));
        addBehaviour(new ReceiveProposalAnalysisBehaviour());

        System.out.println("Environment Agent " + getLocalName() + " is ready.");
    }

    @Override
    protected void takeDown() {
        System.out.println("Environment Agent " + getLocalName() + " is terminating.");
    }

    private class SendProposalToAnalysisBehaviour extends TickerBehaviour {

        @Serial
        private static final long serialVersionUID = 6981101726018053062L;

        public SendProposalToAnalysisBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            ProposalConcept proposal = new ProposalConcept();
            proposal.setTitle("Reforma Tributária");
            proposal.setDescription("Proposta para revisar alíquotas.");

            AnalysisProposalPredicate analysisProposalPredicate = new AnalysisProposalPredicate();
            analysisProposalPredicate.setProposal(proposal);

            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            conversationId = "request-" + System.currentTimeMillis();
            message.setConversationId(conversationId);
            message.addReceiver(getAID("DeputyAgent"));
            message.setLanguage(codec.getName());
            message.setOntology(ontology.getName());

            try {
                getContentManager().fillContent(message, analysisProposalPredicate);
                send(message);
                System.out.println("Requesição: " + message.getContent());
            } catch (Exception e) {
                System.out.println("[Exception in DeputyEnvironmentAgent]: " + e.getMessage());
            }
        }

    }

    private class ReceiveProposalAnalysisBehaviour extends CyclicBehaviour {

        @Serial
        private static final long serialVersionUID = 6981101726018053062L;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId(conversationId)
            );

            try {
                ACLMessage response = receive(mt); // blockReceive()

                if (response != null) {
                    ContentElement content = getContentManager().extractContent(response);

                    if (content != null) {
                        if (content instanceof ApprovedProposalPredicate approvedProposal) {
                            System.out.println("Resposta: " + approvedProposal.getProposal().getTitle() + " aprovada!");
                        } else if (content instanceof RejectedProposalPredicate rejectedProposal) {
                            System.out.println("Resposta: " + rejectedProposal.getProposal().getTitle() + " rejeitada!");
                        } else {
                            System.out.println("Resposta: " + content);
                        }
                    }
                } else {
                    block();
                }
            } catch (Exception e) {
                System.out.println("[Exception in DeputyEnvironmentAgent]: " + e.getMessage());
            }
        }

    }

}
