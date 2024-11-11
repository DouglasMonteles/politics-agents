package org.fga.tcc.enums;

import lombok.Getter;

@Getter
public enum OpenDataEndpoints {

    API_DEPUTES_URL("https://dadosabertos.camara.leg.br/api/v2/deputados"),

    API_VOTING_URL("https://dadosabertos.camara.leg.br/api/v2/votacoes"),

    API_PROPOSAL_URL("https://dadosabertos.camara.leg.br/api/v2/proposicoes"),

    API_PARTY_URL("https://dadosabertos.camara.leg.br/api/v2/partidos"),

    /*
    * In the case of votes, the url used returns the json data based on the year.
    * The url respects the format:
    * https://dadosabertos.camara.leg.br/arquivos/votacoes/json/votacoes-{year}.json
    * */
    API_VOTING_URL_JSON("https://dadosabertos.camara.leg.br/arquivos/votacoes/json/votacoes-"),

    /*
     * In the case of voting object, the url used returns the json data based on the year.
     * The url respects the format:
     * https://dadosabertos.camara.leg.br/arquivos/votacoesObjetos/json/votacoesObjetos-{year}.json
     * */
    API_VOTING_OBJECT_URL_JSON("https://dadosabertos.camara.leg.br/arquivos/votacoesObjetos/json/votacoesObjetos-"),

    /*
     * In the case of proposal, the url used returns the json data based on the year.
     * The url respects the format:
     * https://dadosabertos.camara.leg.br/arquivos/proposicoes/json/proposicoes-{year}.json
     * */
    API_PROPOSAL_URL_JSON("https://dadosabertos.camara.leg.br/arquivos/proposicoes/json/proposicoes-");

    private final String path;

    OpenDataEndpoints(String path) {
        this.path = path;
    }

}
