package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(of = { "partyAcronym" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class VotingOrientation {

    @JsonProperty("orientacaoVoto")
    private String orientationVote;

    @JsonProperty("siglaPartidoBloco")
    private String partyAcronym;

}
