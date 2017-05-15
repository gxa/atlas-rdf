package uk.ac.ebi.spot.atlas.rdf.loader.baseline;

import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExperiment;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfilesList;
import uk.ac.ebi.atlas.profiles.baseline.RnaSeqBaselineProfileStreamFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Named
public class RnaSeqBaselineProfilesLoader {

    private final BaselineProfilesLoader baselineProfilesLoader;

    @Inject
    public RnaSeqBaselineProfilesLoader(RnaSeqBaselineProfileStreamFactory proteomicsBaselineProfileStreamFactory) {
        this.baselineProfilesLoader = new BaselineProfilesLoader(proteomicsBaselineProfileStreamFactory);
    }

    public BaselineProfilesList load (BaselineExperiment experiment) {
        return baselineProfilesLoader.load(experiment);
    }

}
