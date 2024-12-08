package org.fga.tcc.agents;

import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
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
import org.fga.tcc.utils.StringUtils;

import java.io.Serial;
import java.util.*;
import java.util.stream.Stream;

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
            String nickname = "Agent" + StringUtils.removeSpecialCharacters(deputy.getName());

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
        addBehaviour(new SendProposalToAnalysisBehaviour(this, PeriodBehaviour.FIVE_SECONDS.value()));
        addBehaviour(new ReceiveProposalAnalysisBehaviour(agentsNickname));

        System.out.println("Deputy Environment Agent " + getLocalName() + " is ready.");
    }

    private List<Deputy> getDeputes(List<Deputy> deputes) {
        List<Deputy> randomDeputes = new ArrayList<>(deputes);
        Collections.shuffle(randomDeputes);

        return randomDeputes.subList(0, Math.min(10, randomDeputes.size()));
    }

    private DFAgentDescription[] searchDeputyAgents(Agent agent, String serviceType) {
        DFAgentDescription[] result = null;

        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(serviceType);
            dfd.addServices(sd);

            result = DFService.search(agent, dfd);
        } catch (FIPAException e) {
            System.out.println("[FIPAException in DeputyEnvironmentAgent]: " + e.getMessage());
        }

        return result;
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
            proposal.setTitle("Cargos Efetivos");
            proposal.setDescription("Dispõe sobre os cargos efetivos da Carreira Legislativa da Câmara dos Deputados.");
            proposal.setKeywords("Organização administrativa, Cargo efetivo, Carreira legislativa, servidor público, Câmara dos Deputados. _Renomeação, cargo efetivo, atualização, atribuição (carreira pública). _Requisito, provimento de cargo público, nível superior. _Alteração, Resolução da Câmara dos Deputados, Ato da Mesa, revogação, lotação exclusiva. _Extinção, cargo efetivo, Analista Legislativo, Assistente Técnico, Psicólogo. _Alteração, Resolução da Câmara dos Deputados, Departamento de Polícia Legislativa (DEPOL), denominação, Departamento de Polícia Legislativa Federal, atividade típica, Polícia da Câmara dos Deputados. _Requisito, Cargo público, Técnico Legislativo, Policial legislativo federal, prerrogativa.");

            AnalysisProposalPredicate analysisProposalPredicate = new AnalysisProposalPredicate();
            analysisProposalPredicate.setProposal(proposal);

            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            conversationId = "request-" + System.currentTimeMillis();
            message.setConversationId(conversationId);
            message.setLanguage(codec.getName());
            message.setOntology(ontology.getName());


            Stream.of("analyse-proposal-by-pt", "analyse-proposal-by-psol", "analyse-proposal-by-pl").forEach(serviceType -> {
                try {
                    DFAgentDescription[] dfResult = searchDeputyAgents(getAgent(), serviceType);

                    for (DFAgentDescription dfAgentDescription : dfResult) {
                        System.out.println("Agente encontrado: " + dfAgentDescription.getName().getLocalName());
                        message.addReceiver(dfAgentDescription.getName());
                    }

                    getContentManager().fillContent(message, analysisProposalPredicate);
                    send(message);
                } catch (Exception e) {
                    System.out.println("[Exception in DeputyEnvironmentAgent]: " + e.getMessage());
                }
            });
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
