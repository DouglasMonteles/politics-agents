package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Deputy implements Serializable {

    @Serial
    private static final long serialVersionUID = 2462597946368241742L;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("nome")
    private String name;

    @JsonProperty("siglaPartido")
    private String partyAcronym;

    @JsonProperty("idLegislatura")
    private Integer legislatureId;

    @JsonProperty("urlFoto")
    private String pictureUrl;

    @JsonProperty("uri")
    private String uri;

    public Integer getDeputyIdFromUri() {
        int lastBarIndex = this.uri.lastIndexOf('/');
        return Integer.parseInt(this.uri.substring(lastBarIndex + 1));
    }

}
