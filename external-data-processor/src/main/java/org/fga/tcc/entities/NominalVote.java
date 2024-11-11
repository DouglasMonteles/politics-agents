package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(of = { "timestamp" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class NominalVote {

    @JsonProperty("tipoVoto")
    private String vote;

    @JsonProperty("dataRegistroVoto")
    private String timestamp;

    @JsonProperty("deputado_")
    private Deputy deputy;

}
