package uk.ac.ebi.atlas.model;

import uk.ac.ebi.atlas.model.experiment.Experiment;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface CompleteExperiment<T extends Experiment, V extends GeneProfilesList> {

    T getExperiment();
    V getGeneProfilesList();


}
