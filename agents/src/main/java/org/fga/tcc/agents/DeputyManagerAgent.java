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
import jade.lang.acl.UnreadableException;
import jade.wrapper.StaleProxyException;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.exceptions.AgentException;
import org.fga.tcc.observables.Voting;
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

    private Integer deputyAgentsFounded = 0;

    List<Deputy> deputies = null;

    @Override
    protected void setup() {
        // FIPA-SL
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        // Behaviours
        addBehaviour(new BootstrapDeputiesBehaviour());
        addBehaviour(new SendProposalToAnalysisBehaviour(this, PeriodBehaviour.FIVE_SECONDS.value()));
        addBehaviour(new ReceiveProposalAnalysisBehaviour());

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

    private class BootstrapDeputiesBehaviour extends CyclicBehaviour {

        @Serial
        private static final long serialVersionUID = -4340285925206072498L;

        private final AgentService agentService = AgentServiceImpl.getInstance();

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(
                    ACLMessage.REQUEST
            );

            ACLMessage message = receive(mt);
            if (message != null) {
                try {
                    if (message.getContentObject() == null) {
                        return;
                    }

                    deputies = (ArrayList<Deputy>) message.getContentObject();

                    for (Deputy deputy : deputies) {
                        String nickname = "Agent" + StringUtils.removeSpecialCharacters(deputy.getName());

                        var ac = agentService.createAgent(
                                nickname,
                                DeputyAgent.class.getName(),
                                new Object[]{
                                    deputy.getId(),
                                    deputy.getName(),
                                    deputy.getPartyAcronym()
                                }
                        );

                        ac.start();
                    }
                } catch (UnreadableException | StaleProxyException e) {
                    throw new RuntimeException(e);
                }
            } else {
                block();
            }
        }
    }

    private class SendProposalToAnalysisBehaviour extends TickerBehaviour {

        @Serial
        private static final long serialVersionUID = 6981101726018053062L;

        public SendProposalToAnalysisBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (deputies == null || deputies.isEmpty()) {
                return;
            }

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

            List<String> serviceTypes = deputies
                    .stream()
                    .map(deputy -> "analyse-proposal-by-" + StringUtils.removeSpecialCharacters(deputy.getName()).toLowerCase())
                    .toList();

            serviceTypes.forEach(servType -> {
                try {
                    DFAgentDescription[] dfResult = searchDeputyAgents(getAgent(), servType);
                    deputyAgentsFounded = dfResult.length;

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

        private final Voting voting = Voting.getInstance();

        private final Map<String, Integer> votingResult;

        public ReceiveProposalAnalysisBehaviour() {
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
                            voting.setVotes(approvedProposal.getDeputyId(), 1);
                        } else if (content instanceof RejectedProposalPredicate rejectedProposal) {
                            System.out.println("O agente " + sender.getLocalName() + " respondeu: " + rejectedProposal.getProposal().getTitle() + " rejeitada!");
                            votingResult.put(sender.getLocalName(), 0);
                            voting.setVotes(rejectedProposal.getDeputyId(), 0);
                        } else {
                            System.out.println("Resposta: " + content);
                        }

                        System.out.println("Voting result: " + votingResult.size() + " - agents: " + deputyAgentsFounded);

                        if (votingResult.size() == deputyAgentsFounded) {
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
