package org.fga.tcc.facades;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.entities.Proposal;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.services.ProposalService;
import org.fga.tcc.services.VotingService;
import org.fga.tcc.services.impl.ProposalServiceImpl;
import org.fga.tcc.services.impl.VotingServiceImpl;
import org.fga.tcc.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

public class PrepareDatabaseFacade {

    private static final Integer INITIAL_YEAR = 2008;
    private static final Integer FINAL_YEAR = 2023;

    private static final ProposalService proposalService = ProposalServiceImpl.getInstance();
    private static final VotingService voteService = VotingServiceImpl.getInstance();

    private static void downloadVotingAndProposals() {
        for (int year = INITIAL_YEAR; year <= FINAL_YEAR; year++) {
            List<Proposal> proposals = proposalService.getProposalByYear(year);

            for (Proposal proposal : proposals) {
                String proposalId = proposal.getId();
                proposalService.getVotingOfProposalById(proposalId);
            }
        }
    }

    private static void downloadVoteOrientationByParty() {
        Set<String> ids = voteService.getAllVotesIds();
        ids.forEach(voteService::getOrientationAboutTheVoting);
    }

    private static void prepareProposalDescriptionForTraining() {
        String directory = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";

        // Remove linhas repetidas e nivela o numero de linhas dos arquivos .txt
        FileUtils.flattenLinesTxtFile(directory);
        FileUtils.splitFilesInTrainAndTest(directory);
        FileUtils.generateRawVotesToWordVector(directory);
        FileUtils.generateCategoriesFile(directory);
    }

    private static void downloadNominalVotes() {
        for (int year = INITIAL_YEAR; year < FINAL_YEAR; year++) {
            for (Voting voting : voteService.getVotingByYear(year)) {
                voteService.getVotesByVotingId(voting.getId());
            }
        }
    }

    public static void processNominalVotes() {
        // VOTOS INDIVIDUAIS
        downloadNominalVotes();

        String nominalVotesPath = "external-data-processor/src/main/resources/votacoes/votos";

        FileUtils.readFile(nominalVotesPath, (path, mapper) -> {
            String votingId = path.getFileName().toString().replace(".json", "");
            Voting voting = voteService.getVotingById(votingId);

            OpenDataBaseResponseList<NominalVote> openDataBaseResponse = mapper.readValue(path.toFile(), new TypeReference<>() {});
            openDataBaseResponse.getData().forEach(it -> {

            });
        });
    }

    private static void processProposalResumePdfInPlainDataTxt() {
        try {
            String pathProjectPython = System.getProperty("user.dir") + File.separator +
                    "conversor-pdf-to-plain-text" + File.separator;

            String command = "source env/bin/activate && "
                    + "pip install -r " + pathProjectPython + "requirements.txt && "
                    + "python3 " + pathProjectPython + "main.py && "
                    + "echo 'Activated python environment'";

            Process process = new ProcessBuilder()
                    .command("bash", "-c", command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Processo finalizado com código: " + exitCode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void prepareDatabaseForTraining() {
        // -- DOWNLOAD DAS PROPOSIÇÕES E DAS SUAS RESPECTIVAS VOTAÇÕES --
        downloadVotingAndProposals();
        // -- END --

        // -- OBTENDO AS ORIENTAÇÕES DOS VOTOS --
        downloadVoteOrientationByParty();
        // -- END --

        // -- Conversão de PDF --
        processProposalResumePdfInPlainDataTxt();
        // -- END --

        // -- SALVANDO O VOTO (0 ou 1) DE CADA PARTIDO (DESCRICAO DA PROPOSICAO) --
        voteService.generateFavorAndAgainstFilesWithProposalResume();
        // -- END --

        // -- NIVELANDO A QTD DE LINHAS DE CADA TXT PARA O TAMANHO MÁXIMO DE CADA ARQUIVO
        prepareProposalDescriptionForTraining();
        // -- END --

        // processNominalVotes();
        // END
    }

}
