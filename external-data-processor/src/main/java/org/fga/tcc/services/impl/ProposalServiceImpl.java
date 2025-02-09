package org.fga.tcc.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fga.tcc.entities.*;
import org.fga.tcc.enums.OpenDataEndpoints;
import org.fga.tcc.json.FetchJson;
import org.fga.tcc.json.RouterManager;
import org.fga.tcc.services.ProposalService;

import java.util.List;

public class ProposalServiceImpl implements ProposalService {

    private static ProposalService INSTANCE = new ProposalServiceImpl();

    public static ProposalService getInstance() {
        return INSTANCE;
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
