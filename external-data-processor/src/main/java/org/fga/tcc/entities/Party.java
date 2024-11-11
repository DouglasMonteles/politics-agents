package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Party implements Serializable {

    @Serial
    private static final long serialVersionUID = 3044533330174356433L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("sigla")
    private String acronym;

    @JsonProperty("nome")
    private String name;

    @JsonProperty("uri")
    private String uri;

}
