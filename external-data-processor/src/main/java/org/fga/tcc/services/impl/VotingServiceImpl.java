package org.fga.tcc.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fga.tcc.entities.*;
import org.fga.tcc.enums.OpenDataEndpoints;
import org.fga.tcc.json.FetchJson;
import org.fga.tcc.json.RouterManager;
import org.fga.tcc.services.PartyService;
import org.fga.tcc.services.ProposalService;
import org.fga.tcc.services.VotingService;
import org.fga.tcc.utils.FileUtils;
import org.fga.tcc.utils.ResourceUtils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VotingServiceImpl implements VotingService {

    private static final VotingService INSTANCE = new VotingServiceImpl();

    public static VotingService getInstance() {
        return INSTANCE;
    }

    @Override
    public Voting getVotingById(String votingId) {
        RouterManager routerManager = new RouterManager();
        FetchJson<Voting> fetchJson = new FetchJson<>();
        OpenDataBaseSingleResponse<Voting> voteOpenDataBaseResponse = fetchJson.get(
                new TypeReference<>() {},
                routerManager
                        .setUrl(OpenDataEndpoints.API_VOTING_URL.getPath())
                        .setRequestParamId(votingId)
                        .getUrl()
        );

        return voteOpenDataBaseResponse.getData();
    }

    @Override
    public List<Voting> getVotingByYear(int year) {
        RouterManager routerManager = new RouterManager();
        FetchJson<Voting> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<Voting> voteOpenDataBaseResponse = fetchJson.getJson(
                routerManager
                        .setUrl(OpenDataEndpoints.API_VOTING_URL_JSON.getPath())
                        .setJsonName(String.valueOf(year))
                        .getUrl(),
                new TypeReference<>() {
                }
        );

        return voteOpenDataBaseResponse.getData();
    }

    @Override
    public List<NominalVote> getVotesByVotingId(String votingId) {
        RouterManager routerManager = new RouterManager();
        FetchJson<NominalVote> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<NominalVote> voteOpenDataBaseResponse = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_VOTING_URL.getPath())
                        .setRequestParamId(votingId)
                        .setRequestUri("votos")
                        .getUrl(),
                new TypeReference<>() {
                }
        );

        // Voting does not have nominal votes
        if (voteOpenDataBaseResponse == null) {
            return List.of();
        }

        return voteOpenDataBaseResponse.getData();
    }

    @Override
    public List<VotingObject> getVotingObjectByYear(Integer year) {
        RouterManager routerManager = new RouterManager();
        FetchJson<VotingObject> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<VotingObject> votingObjectOpenDataBaseResponse = fetchJson.getJson(
                routerManager
                        .setUrl(OpenDataEndpoints.API_VOTING_OBJECT_URL_JSON.getPath())
                        .setJsonName(String.valueOf(year))
                        .getUrl(),
                new TypeReference<>() {
                }
        );

        return votingObjectOpenDataBaseResponse.getData();
    }

    public List<String> getAllVotingIdsWithNominalVotes() {
        String directoryPath = "external-data-processor/src/main/resources/votacoes/votos";
        List<String> votingIds = new ArrayList<>();

        try {
            Path dirPath = Paths.get(directoryPath);
            DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.json");
            ObjectMapper objectMapper = new ObjectMapper();

            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int dotIndex = fileName.indexOf('.');

                String fileNameWithoutExtension = fileName.substring(0, dotIndex);

                votingIds.add(fileNameWithoutExtension);
            }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
        }

        return votingIds;
    }

    @Override
    public List<VotingOrientation> getOrientationAboutTheVoting(String votingId) {
        RouterManager routerManager = new RouterManager();
        FetchJson<VotingOrientation> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<VotingOrientation> votingOrientationOpenDataBaseResponseList = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_VOTING_URL.getPath())
                        .setRequestParamId(votingId)
                        .setRequestUri("orientacoes")
                        .getUrl(),
                new TypeReference<>() {
                }
        );

        return votingOrientationOpenDataBaseResponseList.getData();
    }

    public Set<String> getAllVotesIds() {
        String filePath = "external-data-processor/src/main/resources/proposicoes/votacoes";
        Set<String> ids = new HashSet<>();

        FileUtils.readFile(filePath, (path, mapper) -> {
            OpenDataBaseResponseList<Voting> openDataBaseResponse = mapper.readValue(path.toFile(), new TypeReference<>() {});
            openDataBaseResponse.getData().forEach(it -> ids.add(it.getId()));
        });

        return ids;
    }

    public void generateFavorAndAgainstFilesWithProposalResume() {
        VotingService voteService = new VotingServiceImpl();
        PartyService partyService = new PartyServiceImpl();

        String directoryPath = "external-data-processor/src/main/resources/votacoes/orientacoes";
        String pureDataPath = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalDescription";

        List<String> partyAcronyms = partyService.getParties().stream()
                .map(Party::getAcronym)
                .toList();

        FileUtils.readFile(directoryPath, (path, mapper) -> {
            File orientationVote = path.toFile();
            String votingId = orientationVote.getName().split("\\.")[0];
            Voting voting = voteService.getVotingById(votingId);
            OpenDataBaseResponseList<VotingOrientation> openDataBaseResponse = mapper.readValue(orientationVote, new TypeReference<>() {});
            openDataBaseResponse.getData().forEach(voteOrientation -> {
                String partyAcronym = voteOrientation.getPartyAcronym()
                        .replaceAll("\\*", "")
                        .replaceAll("Bl ", "")
                        .replaceAll("Fdr ", "")
                        .replaceAll("Solidaried", "Solidariedade")
                        .trim();
                char vote = voteOrientation.getOrientationVote().equalsIgnoreCase("sim") ? '1' : '0';

                for (String party : partyAcronyms) {
                    if (partyAcronym.toLowerCase().contains(party.toLowerCase())) {
                        String partyPath = pureDataPath + File.separator + party + File.separator + vote + ".txt";

                        FileUtils.writeFile(partyPath, (writer) -> {
                            String proposalId = voting.getProposal().getProposalIdByUri();
                            String description = voting.getProposal().getDescription();

                            if (description != null && !description.isEmpty()) {
                                if (description.length() <= 1)
                                    return;

                                description = normalizeProposalResume(description);
                                String directory = System.getProperty("user.dir") + "/conversor-pdf-to-plain-text/proposals_txt/" + proposalId + ".txt";

                                if (FileUtils.isFileAlreadyCreated(directory)) {
                                    try (BufferedReader br = new BufferedReader(new FileReader(directory))) {
                                        String temp;
                                        while ((temp = br.readLine()) != null) {
                                            String row = normalizeProposalResume(temp);
                                            if (row.length() > 1) {
                                                writer.write(row);
                                                writer.newLine();
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("File Exception : " + e.getMessage());
                                    }
                                    if (description.length() > 1)
                                        writer.write(description);
                                } else {
                                    if (description.length() > 1) {
                                        writer.write(description);
                                        writer.newLine();
                                    }
                                }
                            } else {
                                System.out.println("Sem descrição. Voting id: " + voting.getId());
                            }

                            writer.close();
                        });
                    }
                }
            });
        });
    }

    public void generateDataAboutPartyProposalKeywords() {
        VotingService voteService = new VotingServiceImpl();
        PartyService partyService = new PartyServiceImpl();
        ProposalService proposalService = new ProposalServiceImpl();

        String directoryPath = "external-data-processor/src/main/resources/votacoes/orientacoes";
        String pureDataPath = System.getProperty("user.dir") + "/trained-data/votes/partyOrientation/proposalKeywords";

        List<String> partyAcronyms = partyService.getParties().stream()
                .map(Party::getAcronym)
                .toList();

        FileUtils.readFile(directoryPath, (path, mapper) -> {
            File orientationVote = path.toFile();
            String votingId = orientationVote.getName().split("\\.")[0];
            Voting voting = voteService.getVotingById(votingId);
            OpenDataBaseResponseList<VotingOrientation> openDataBaseResponse = mapper.readValue(orientationVote, new TypeReference<>() {});
            openDataBaseResponse.getData().forEach(voteOrientation -> {
                String partyAcronym = voteOrientation.getPartyAcronym()
                        .replaceAll("\\*", "")
                        .replaceAll("Bl ", "")
                        .replaceAll("Fdr ", "")
                        .replaceAll("Solidaried", "Solidariedade")
                        .trim();
                char vote = voteOrientation.getOrientationVote().equalsIgnoreCase("sim") ? '1' : '0';

                for (String party : partyAcronyms) {
                    if (partyAcronym.toLowerCase().contains(party.toLowerCase())) {
                        String partyPath = pureDataPath + File.separator + party + File.separator + vote + ".txt";

                        FileUtils.writeFile(partyPath, (writer) -> {
                            String proposalId = voting.getProposal().getProposalIdByUri();

                            if (proposalId != null) {
                                Proposal proposal = proposalService.getProposalById(proposalId);

                                if (proposal != null && proposal.getKeyWords() != null) {
                                    String keywords = proposal.getKeyWords()
                                            .replaceAll("_", "")
                                            .trim();

                                    if (!keywords.isEmpty()) {
                                        writer.write(keywords);
                                        writer.newLine();
                                    }
                                } else {
                                    System.out.println("Sem descrição. Voting id: " + voting.getId());
                                }
                            }

                            writer.close();
                        });
                    }
                }
            });
        });
    }


    private String normalizeProposalResume(String description) {
        if (description.length() < 2) {
            return "";
        }

        return description
                .replaceAll("[\\r\\n]", "")
                .replaceAll("\\n", "")
                .replaceAll("<missing-text>", "")
                .replaceAll("GLYPH<\\d+>", "")
                .replaceAll("/g\\d+", "")
                .replaceAll("/\\d+", "")
                .replaceAll("/.notdef", "")
                .replaceAll("\\\\_", "")
                .replaceAll("\\.", "\n")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\$", "")
                .replaceAll("#", "")
                .replaceAll("\\*", "")
                .replaceAll("&", "")
                .replaceAll("%", "")
                .replaceAll("\"", "")
                .replaceAll("\\b\\d\\b", "")
                .replaceAll("\\+", "")
                .replaceAll("/", "")
                .replaceAll(":", "")
                .replaceAll(";", "")
                .replaceAll("\\?", "")
                .replaceAll("=", "")
                .replaceAll("!", "")
                .replaceAll("@", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .trim();
    }

    @Deprecated
    public void savePureData() {
        String path = ResourceUtils.RESOURCE_TRAINING_PURE_DATA_PATH + File.separator + "deputies" + File.separator + "todos" + File.separator + "todos" + ".txt";

        if (!FileUtils.isFileAlreadyCreated(path)) {
            FileUtils.createFile(path);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (VotingObject votingObject : getVotingObjectByYear(2008)) {
                String proposalDescription = votingObject.getProposal().getSummary().replaceAll("\n", "");
                if (!proposalDescription.isEmpty()) {
                    writer.write(proposalDescription);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Arquivo gravado com sucesso!");
        }
    }

    private String getNormalizedSpeechType(DeputySpeech deputySpeech) {
        /*
         * If we use a cont variable, it's possible that the two or more
         * speeches (different deputies) has the same cont. Then, in this case,
         * the timestamp will be used instead.
         * */
        long timestamp = System.currentTimeMillis();

        String normalizedSpeechType = deputySpeech.getSpeechType()
                .replaceAll(" ", "_")
                .replaceAll("Í", "I")
                .replaceAll("Ã", "A")
                .replaceAll("Ç", "C")
                .replaceAll("Õ", "O")
                .replaceAll("Ê", "E")
                .trim();

        return ResourceUtils.RESOURCE_TRAINING_PURE_DATA_PATH + "/" + "deputies/speechesTypes/" + normalizedSpeechType + "/" + timestamp + ".txt";
    }

}
