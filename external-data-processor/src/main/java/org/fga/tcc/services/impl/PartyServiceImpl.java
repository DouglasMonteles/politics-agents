package org.fga.tcc.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.entities.Party;
import org.fga.tcc.enums.OpenDataEndpoints;
import org.fga.tcc.json.FetchJson;
import org.fga.tcc.json.RouterManager;
import org.fga.tcc.services.PartyService;

import java.util.List;

public class PartyServiceImpl implements PartyService {

    @Override
    public List<Party> getParties() {
        RouterManager routerManager = new RouterManager();
        FetchJson<Party> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<Party> deputeOpenDataBaseResponse = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_PARTY_URL.getPath())
                        .setPage(1)
                        .setItems(1000)
                        .setOrderBy("nome")
                        .getUrl(),
                new TypeReference<>() {}
        );

        return deputeOpenDataBaseResponse.getData();
    }

}
