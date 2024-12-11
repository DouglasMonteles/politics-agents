package org.fga.tcc.agents;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
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
import org.fga.tcc.utils.StringUtils;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = {"deputyName", "partyAcronym"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DeputyAgent extends Agent {

    private final SLCodec codec = new SLCodec();
    private final Ontology ontology = DeputyOntology.getInstance();

    @Serial
    private static final long serialVersionUID = -7061619046169059737L;

    private Integer deputyId;
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

        if (args == null || args.length < 3) {
            throw new AgentException("Args is null or its length is not enough.");
        }

        this.deputyId = (Integer) args[0];
        this.deputyName = (String) args[1];
        this.partyAcronym = (String) args[2];

        System.out.println("Deputy " + getLocalName() + " is ready");
        System.out.println("Nome: " + deputyName);
        System.out.println("Partido: " + partyAcronym);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sdAnalyseProposalByPartyAcronym = new ServiceDescription();
        sdAnalyseProposalByPartyAcronym.setType("analyse-proposal-by-" + partyAcronym.toLowerCase());
        sdAnalyseProposalByPartyAcronym.setName("Analyse-of-Proposal-By-Party-Acronym");

        ServiceDescription sdAnalyseProposalByDeputy = new ServiceDescription();
        sdAnalyseProposalByDeputy.setType("analyse-proposal-by-" + StringUtils.removeSpecialCharacters(deputyName).toLowerCase());
        sdAnalyseProposalByDeputy.setName("Analyse-of-Proposal-By-Deputy-Name");

        dfd.addServices(sdAnalyseProposalByPartyAcronym);
        dfd.addServices(sdAnalyseProposalByDeputy);

        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + " has registered the service: " + sdAnalyseProposalByPartyAcronym.getType());
            System.out.println(getLocalName() + " has registered the service: " + sdAnalyseProposalByDeputy.getType());
        } catch (FIPAException e) {
            System.out.println("[DeputyAgent] Error: " + e.getMessage());
        }

        addBehaviour(new VotingBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " has been removed from DF.");
        } catch (FIPAException e) {
            System.out.println("[DeputyAgent] Error: " + e.getMessage());
        }
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

                        Map<String, Integer[]> response = new HashMap<>();
                        response.put("description", new Integer[]{0, 0});
                        response.put("keywords", new Integer[]{0, 0});

                        // TODO: Analyze proposal
                        for (int attempts = 1; attempts <= 3; attempts++) {
                            String resultProposalDescription = votingModelService
                                    .setModelPath(System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription/" + deputyAgent.partyAcronym)
                                    .evaluateVoteModel(proposal.getDescription());

                            String resultProposalKeywords = votingModelService
                                    .setModelPath(System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalKeywords/" + deputyAgent.partyAcronym)
                                    .evaluateVoteModel(proposal.getKeywords());

                            Integer[] votesDesc = response.get("description");
                            Integer[] votesKey = response.get("keywords");

                            if (resultProposalDescription.equalsIgnoreCase("favor")) {
                                response.put("description", new Integer[]{ votesDesc[0]+1, votesDesc[1] });
                            } else {
                                response.put("description", new Integer[]{ votesDesc[0], votesDesc[1]+1 });
                            }

                            if (resultProposalKeywords.equalsIgnoreCase("favor")) {
                                response.put("keywords", new Integer[]{ votesKey[0]+1, votesKey[1] });
                            } else {
                                response.put("keywords", new Integer[]{ votesKey[0], votesKey[1]+1 });
                            }
                        }

                        ACLMessage message = requestMessage.createReply();
                        message.setPerformative(ACLMessage.INFORM);
                        message.setLanguage(codec.getName());
                        message.setOntology(ontology.getName());

                        float descriptionWeight = 0.8f;
                        float keywordsWeight = 0.2f;

                        float favor = (response.get("keywords")[0] * keywordsWeight) + (response.get("description")[0] * descriptionWeight);
                        float against = (response.get("keywords")[1] * keywordsWeight) + (response.get("description")[1] * descriptionWeight);

                        String result = favor > against ? "favor" : "against";

                        if (result.equalsIgnoreCase("favor")) {
                            ApprovedProposalPredicate approvedProposal = new ApprovedProposalPredicate();
                            approvedProposal.setDeputyId(deputyId);
                            approvedProposal.setProposal(proposal);
                            getContentManager().fillContent(message, approvedProposal);
                            //System.out.println("Deputado " + deputyAgent.deputyName + " aprovou");
                        } else {
                            RejectedProposalPredicate rejectedProposal = new RejectedProposalPredicate();
                            rejectedProposal.setDeputyId(deputyId);
                            rejectedProposal.setProposal(proposal);
                            getContentManager().fillContent(message, rejectedProposal);
                            //System.out.println("Deputado " + deputyAgent.deputyName + " rejeitou");
                        }

                        send(message);
                    }
                } catch (Exception e) {
                    System.out.println("[DeputyAgent] Error: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

}
