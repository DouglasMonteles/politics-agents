package org.fga.tcc.ontologies.predicate;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fga.tcc.ontologies.concept.ProposalConcept;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectedProposalPredicate implements Predicate {

    @Serial
    private static final long serialVersionUID = 7274995000373287349L;

    private Integer deputyId;

    private ProposalConcept proposal;

    @Slot(mandatory = true)
    public Integer getDeputyId() {
        return deputyId;
    }

    @Slot(mandatory = true)
    public ProposalConcept getProposal() {
        return proposal;
    }

}
