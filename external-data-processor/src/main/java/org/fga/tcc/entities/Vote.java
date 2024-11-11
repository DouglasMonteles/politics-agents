package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Vote {

    @JsonProperty("id")
    private String id;

    @JsonProperty("dataHoraRegistro")
    private String timestamp;

    @JsonProperty("aprovacao")
    private Integer approved;

    @JsonProperty("proposicoesAfetadas")
    private List<Proposal> affectedProposals;

}
