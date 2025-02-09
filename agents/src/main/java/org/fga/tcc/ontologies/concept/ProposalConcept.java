package org.fga.tcc.ontologies.concept;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalConcept implements Concept {

    @Serial
    private static final long serialVersionUID = 2582492444780163283L;

    private String title;
    private String description;
    private String keywords;

    @Slot(mandatory = false)
    public String getTitle() {
        return title;
    }

    @Slot(mandatory = true)
    public String getDescription() {
        return description;
    }

    @Slot(mandatory = false)
    public String getKeywords() {
        return keywords;
    }

}
