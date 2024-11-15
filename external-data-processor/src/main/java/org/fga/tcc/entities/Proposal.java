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
public class Proposal {

    @JsonProperty("id")
    private String id;

    @JsonProperty("ementa")
    private String summary;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("keywords")
    private String keyWords;

    @JsonProperty("uriProposicaoCitada")
    private String uri;

    public String getProposalIdByUri() {
        if (this.uri == null) {
            return null;
        }
        int index = this.uri.lastIndexOf('/');
        return this.uri.substring(index + 1);
    }

}
