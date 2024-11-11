package org.fga.tcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "uriEvent" })
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeputySpeech {

    @JsonProperty("uriEvento")
    private String uriEvent;

    @JsonProperty("dataInicial")
    private String initialDate;

    @JsonProperty("dataFinal")
    private String finalDate;

    @JsonProperty("tipoDiscurso")
    private String speechType;

    @JsonProperty("urlTexto")
    private String textUrl;

    @JsonProperty("sumario")
    private String summary;

    @JsonProperty("transcricao")
    private String transcription;

}
