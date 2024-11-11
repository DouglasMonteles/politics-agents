package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class VotingObject {

    @JsonProperty("idVotacao")
    private String votingId;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("proposicao_")
    private Proposal proposal;

}
