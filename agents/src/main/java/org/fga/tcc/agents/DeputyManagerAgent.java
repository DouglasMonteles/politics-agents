package org.fga.tcc.agents;

import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.exceptions.AgentException;
import org.fga.tcc.ontologies.DeputyOntology;
import org.fga.tcc.ontologies.concept.ProposalConcept;
import org.fga.tcc.ontologies.predicate.AnalysisProposalPredicate;
import org.fga.tcc.ontologies.predicate.ApprovedProposalPredicate;
import org.fga.tcc.ontologies.predicate.RejectedProposalPredicate;
import org.fga.tcc.services.AgentService;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.impl.AgentServiceImpl;
import org.fga.tcc.services.impl.DeputyServiceImpl;

import java.io.Serial;
import java.util.*;

public class DeputyManagerAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -1951592876859134303L;

    private final SLCodec codec = new SLCodec();
    private final Ontology ontology = DeputyOntology.getInstance();

    private String conversationId;

    List<String> agentsNickname = new ArrayList<>();

    @Override
    protected void setup() {
        AgentService agentService = AgentServiceImpl.getInstance();
        DeputyService deputyService = DeputyServiceImpl.getInstance();

        // FIPA-SL
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        // Agents
        List<Deputy> deputies = getDeputes(deputyService.getDeputes());

        for (Deputy deputy : deputies) {
            String nickname = "Agent" + deputy
                    .getName()
                    .replaceAll(" ", "")
                    .replaceAll("é", "e")
                    .replaceAll("É", "E")
                    .replaceAll("á", "a")
                    .replaceAll("Á", "A")
                    .replaceAll("ã", "a")
                    .replaceAll("ú", "u")
                    .replaceAll("ô", "o")
                    .replaceAll("\\.", "");

            agentsNickname.add(nickname);

            var ac = agentService.createAgent(
                    nickname,
                    DeputyAgent.class.getName(),
                    new Object[] { deputy.getName(), deputy.getPartyAcronym() }
            );

            try {
                ac.start();
            } catch (StaleProxyException e) {
                throw new AgentException(e.getMessage());
            }
        }


        // Behaviours
        addBehaviour(new SendProposalToAnalysisBehaviour(this, agentsNickname, PeriodBehaviour.FIVE_SECONDS.value()));
        addBehaviour(new ReceiveProposalAnalysisBehaviour(agentsNickname));

        System.out.println("Deputy Environment Agent " + getLocalName() + " is ready.");
    }

    private List<Deputy> getDeputes(List<Deputy> deputes) {
        List<Deputy> randomDeputes = new ArrayList<>(deputes);
        Collections.shuffle(randomDeputes);

        return randomDeputes.subList(0, Math.min(10, randomDeputes.size()));
    }

    @Override
    protected void takeDown() {
        System.out.println("Environment Agent " + getLocalName() + " is terminating.");
    }

    private class SendProposalToAnalysisBehaviour extends TickerBehaviour {

        @Serial
        private static final long serialVersionUID = 6981101726018053062L;

        private final List<String> agentsNickname;

        public SendProposalToAnalysisBehaviour(Agent a, List<String> agentsNickname, long period) {
            super(a, period);
            this.agentsNickname = agentsNickname;
        }

        @Override
        protected void onTick() {
            ProposalConcept proposal = new ProposalConcept();
            proposal.setTitle("Reforma Tributária");
            proposal.setDescription("Proposta para revisar alíquotas.");
            proposal.setKeywords("alíquotas, economia, revisão, reforma tributária");

            AnalysisProposalPredicate analysisProposalPredicate = new AnalysisProposalPredicate();
            analysisProposalPredicate.setProposal(proposal);

            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            conversationId = "request-" + System.currentTimeMillis();
            message.setConversationId(conversationId);
            message.setLanguage(codec.getName());
            message.setOntology(ontology.getName());

            agentsNickname.forEach(nickname -> message.addReceiver(getAID(nickname)));

            try {
                getContentManager().fillContent(message, analysisProposalPredicate);
                send(message);
                // System.out.println("Requesição: " + message.getContent());
            } catch (Exception e) {
                System.out.println("[Exception in DeputyEnvironmentAgent]: " + e.getMessage());
            }
        }

    }

    private class ReceiveProposalAnalysisBehaviour extends CyclicBehaviour {

        @Serial
        private static final long serialVersionUID = 6981101726018053062L;

        private final List<String> expectedSenders;

        private final Map<String, Integer> votingResult;

        public ReceiveProposalAnalysisBehaviour(List<String> expectedSenders) {
            this.expectedSenders = expectedSenders;
            this.votingResult = new HashMap<>();
        }

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId(conversationId)
            );

            try {
                ACLMessage response = receive(mt); // blockReceive()

                if (response != null) {
                    AID sender = response.getSender();
                    ContentElement content = getContentManager().extractContent(response);

                    if (content != null) {
                        if (content instanceof ApprovedProposalPredicate approvedProposal) {
                            System.out.println("O agente " + sender.getLocalName() + " respondeu: " + approvedProposal.getProposal().getTitle() + " aprovada!");
                            votingResult.put(sender.getLocalName(), 1);
                        } else if (content instanceof RejectedProposalPredicate rejectedProposal) {
                            System.out.println("O agente " + sender.getLocalName() + " respondeu: " + rejectedProposal.getProposal().getTitle() + " rejeitada!");
                            votingResult.put(sender.getLocalName(), 0);
                        } else {
                            System.out.println("Resposta: " + content);
                        }

                        if (votingResult.size() == expectedSenders.size()) {
                            long favor = votingResult.values().stream().filter(it -> it == 1).count();
                            long against = votingResult.values().stream().filter(it -> it == 0).count();

                            System.out.println("Favor: " + favor + " | Contra: " + against + " | Total: " + votingResult.size());

                            votingResult.clear();
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
