package uk.ac.ebi.spot.rdf.builder;

import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.experiment.Experiment;

import java.io.OutputStream;

/**
 * @author Simon Jupp
 * @date 02/05/2014
 * Samples Phenotypes and Ontologies Team,  EMBL-EBI
 */
public interface ExperimentBuilder <T extends Experiment, V extends GeneProfilesList> {

    /**
     * Build an RDF model from an atlas experiment and associated genelist
     * @param experiment
     * @param profile
     */
    void build(T experiment,V profile);

    /**
     * Flush RDF model to an output stream in chosen RDF syntax
     * @param stream
     */
    void flushModel (OutputStream stream, String format);

}
