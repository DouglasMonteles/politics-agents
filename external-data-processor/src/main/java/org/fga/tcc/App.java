package org.fga.tcc;

import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.DeputyFront;
import org.fga.tcc.entities.DeputySpeech;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.ProposalService;
import org.fga.tcc.services.VoteService;
import org.fga.tcc.services.impl.DeputyServiceImpl;
import org.fga.tcc.services.impl.ProposalServiceImpl;
import org.fga.tcc.services.impl.VoteServiceImpl;
import org.fga.tcc.utils.FileUtils;

import java.io.File;
import java.util.List;

public class App {
    public static void main( String[] args ) {
        DeputyService deputeService = new DeputyServiceImpl();
        ProposalService proposalService = new ProposalServiceImpl();
        VoteService voteService = new VoteServiceImpl();

        // -- DOWNLOAD DAS FRENTES DOS DEPUTADOS --
//        for (Deputy deputy : deputeService.getDeputes()) {
//            deputeService.getDeputyFront(deputy.getId());
//        }
        // -- END --

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

        // -- SALVANDO O VOTO (0 ou 1) DE CADA PARTIDO (DESCRICAO DA PROPOSICAO) --
//        voteService.generateDataAboutPartyOrientation();
//        // -- END --
//
//        // -- NIVELANDO A QTD DE LINHAS DE CADA TXT PARA O TAMANHO MÁXIMO DE CADA ARQUIVO
//        String directory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";
//
//        // Remove linhas repetidas e nivela o numero de linhas dos arquivos .txt
//        FileUtils.flattenLinesTxtFile(directory);
////
//        FileUtils.splitFilesInTrainAndTest(directory);
//
//        FileUtils.generateRawVotesToWordVector(directory);

//        FileUtils.generateCategoriesFile(directory);
        // -- END --

        // -- SALVANDO O VOTO (0 ou 1) DE CADA PARTIDO (DESCRICAO DA PROPOSICAO) --
//        voteService.generateDataAboutPartyProposalKeywords();
        // -- END --

        // -- NIVELANDO A QTD DE LINHAS DE CADA TXT PARA O TAMANHO MÁXIMO DE CADA ARQUIVO
        String directory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalKeywords";

        // Remove linhas repetidas e nivela o numero de linhas dos arquivos .txt
//        FileUtils.flattenLinesTxtFile(directory);
//
        FileUtils.splitFilesInTrainAndTest(directory);
//
//        FileUtils.generateRawVotesToWordVector(directory);

        //FileUtils.generateCategoriesFile(directory);
        // -- END --
    }
}
