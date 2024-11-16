package org.fga.tcc.ontologies;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import org.fga.tcc.ontologies.concept.ProposalConcept;
import org.fga.tcc.ontologies.predicate.AnalysisProposalPredicate;
import org.fga.tcc.ontologies.predicate.ApprovedProposalPredicate;
import org.fga.tcc.ontologies.predicate.RejectedProposalPredicate;

import java.io.Serial;

public class DeputyOntology extends BeanOntology {

    public static final String ONTOLOGY_NAME = "DeputyOntology";

    @Serial
    private static final long serialVersionUID = -87578988358461428L;

    private static final DeputyOntology DEPUTY_ONTOLOGY = new DeputyOntology(ONTOLOGY_NAME);

    private DeputyOntology(String name) {
        super(name);

        try {
            add(ProposalConcept.class);
            add(AnalysisProposalPredicate.class);
            add(ApprovedProposalPredicate.class);
            add(RejectedProposalPredicate.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Ontology getInstance() {
        return DEPUTY_ONTOLOGY;
    }

}
