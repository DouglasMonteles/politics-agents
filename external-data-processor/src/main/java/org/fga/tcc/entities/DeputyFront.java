package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DeputyFront {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("titulo")
    private String title;

    @JsonProperty("uri")
    private String uri;

}
