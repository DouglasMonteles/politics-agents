package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Voting {

    @JsonProperty("id")
    private String id;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("dataHoraRegistro")
    private String timestamp;

    @JsonProperty("uriOrgao")
    private String uriAgency;

    @JsonProperty("uriEvento")
    private String uriEvent;

    @JsonProperty("aprovacao")
    private Byte approved;

    /*
     * May have the string: Sim 4; não 349; abstenção 1; total 354.
     * */
    @JsonProperty("descricao")
    private String description;

    @JsonProperty("ultimaApresentacaoProposicao")
    private Proposal proposal;

    @JsonProperty("proposicoesAfetadas")
    private List<Proposal> affectedProposals = new ArrayList<>();

}
