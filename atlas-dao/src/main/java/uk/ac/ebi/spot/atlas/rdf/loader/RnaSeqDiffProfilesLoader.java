package uk.ac.ebi.spot.atlas.rdf.loader;

import uk.ac.ebi.atlas.commons.streams.ObjectInputStream;
import uk.ac.ebi.atlas.experimentpage.context.RnaSeqRequestContext;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialProfilesList;
import uk.ac.ebi.atlas.model.experiment.differential.rnaseq.RnaSeqProfile;
import uk.ac.ebi.atlas.profiles.differential.rnaseq.RnaSeqProfileStreamFactory;
import uk.ac.ebi.atlas.web.DifferentialRequestPreferences;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class RnaSeqDiffProfilesLoader {

    private RnaSeqProfileStreamFactory diffProfileStreamFactory;

    @Inject
    public RnaSeqDiffProfilesLoader(RnaSeqProfileStreamFactory rnaSeqProfileStreamFactory) {
        this.diffProfileStreamFactory = rnaSeqProfileStreamFactory;
    }

    public DifferentialProfilesList<RnaSeqProfile> load (DifferentialExperiment experiment) {
        DifferentialRequestPreferences preferences = new DifferentialRequestPreferences();

        ObjectInputStream<RnaSeqProfile> stream = diffProfileStreamFactory.create(experiment, new RnaSeqRequestContext(preferences, experiment));
        Collection<RnaSeqProfile> profiles = new HashSet<RnaSeqProfile>();
        RnaSeqProfile profile1;
        while ( (profile1 = stream.readNext()) != null) {
            profiles.add(profile1);
        }
        return new DifferentialProfilesList<>(profiles);
    }
}
