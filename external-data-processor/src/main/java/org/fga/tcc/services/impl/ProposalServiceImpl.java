package org.fga.tcc.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fga.tcc.entities.*;
import org.fga.tcc.enums.OpenDataEndpoints;
import org.fga.tcc.json.FetchJson;
import org.fga.tcc.json.RouterManager;
import org.fga.tcc.services.ProposalService;
import org.fga.tcc.services.VoteService;
import org.fga.tcc.utils.FileUtils;
import org.fga.tcc.utils.ResourceUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ProposalServiceImpl implements ProposalService {

    public static void main(String[] args) {
        ProposalService proposalService = new ProposalServiceImpl();
        VoteService voteService = new VoteServiceImpl();

        // -- DOWNLOAD DAS PROPOSIÇÕES E DAS SUAS RESPECTIVAS VOTAÇÕES --
//        for (int year = 2023; year < 2024; year++) {
//            List<Proposal> proposals = proposalService.getProposalByYear(year);
//
//            for (Proposal proposal : proposals) {
//                String proposalId = proposal.getId();
//                List<Voting> votingList = proposalService.getVotingOfProposalById(proposalId);
//            }
//        }
        // -- END --

        // -- OBTENDO AS ORIENTAÇÕES DOS VOTOS --
//        Set<String> ids = voteService.getAllVotesIds();
//        ids.forEach(voteService::getOrientationAboutTheVoting);
        // -- END --

        // -- SALVANDO O VOTO DE CADA PARTIDO --
        voteService.generateDataAboutPartyOrientation();
        // -- END --


//        List<Proposal> proposals = proposalService.getProposalByYear(2008);
//
//        String path = ResourceUtils.RESOURCE_TRAINING_PURE_DATA_PATH +
//                File.separator +
//                "proposals" +
//                File.separator +
//                "keywords" +
//                File.separator +
//                "votes" +
//                File.separator +
//                "party";
//
//        for (Proposal proposal : proposals) {
//            String proposalId = proposal.getId();
//            List<Voting> votingList = proposalService.getVotingOfProposalById(proposalId);
//
//            if (!votingList.isEmpty()) {
//                Voting voting = votingList.getFirst();
//
//                String votingId = voting.getId();
//                List<VotingOrientation> votingOrientations = voteService.getOrientationAboutTheVoting(votingId);
//
////                votingOrientations.forEach(it -> {
////                    String vote = it.getOrientationVote().equalsIgnoreCase("sim") ? "1" : "0";
////                    String path2 = path
////                            + File.separator +
////                            it.getPartyAcronym() +
////                            File.separator +
////                            vote +
////                            File.separator +
////                            "result.txt";
////
////                    FileUtils.writeFile(path2, (writer) -> {
////                        writer.write(proposal.getSummary()
////                                .replaceAll("_ ", "")
////                                .replaceAll("\n", " ")
////                                .replaceAll("_", "")
////                        );
////                        writer.newLine();
////                        writer.close();
////                    });
////                });
//            }
//        }
    }

    public List<Proposal> getProposalByYear(int year) {
        RouterManager routerManager = new RouterManager();
        FetchJson<Proposal> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<Proposal> proposalOpenDataBaseResponse = fetchJson.getJson(
                routerManager
                        .setUrl(OpenDataEndpoints.API_PROPOSAL_URL_JSON.getPath())
                        .setJsonName(String.valueOf(year))
                        .getUrl(),
                new TypeReference<>() {}
        );

        return proposalOpenDataBaseResponse.getData();
    }

    public Proposal getProposalById(String id) {
        RouterManager routerManager = new RouterManager();
        FetchJson<Proposal> fetchJson = new FetchJson<>();
        OpenDataBaseSingleResponse<Proposal> proposalOpenDataBaseResponse = fetchJson.get(
                new TypeReference<>() {},
                routerManager
                        .setUrl(OpenDataEndpoints.API_PROPOSAL_URL.getPath())
                        .setRequestParamId(id)
                        .getUrl()
        );

        if (proposalOpenDataBaseResponse == null) {
            return null;
        }

        return proposalOpenDataBaseResponse.getData();
    }

    @Override
    public List<Voting> getVotingOfProposalById(String id) {
        RouterManager routerManager = new RouterManager();
        FetchJson<Voting> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<Voting> proposalOpenDataBaseResponse = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_PROPOSAL_URL.getPath())
                        .setRequestParamId(id)
                        .setRequestUri("votacoes")
                        .getUrl(),
                new TypeReference<>() {}
        );

        return proposalOpenDataBaseResponse.getData();
    }
}
