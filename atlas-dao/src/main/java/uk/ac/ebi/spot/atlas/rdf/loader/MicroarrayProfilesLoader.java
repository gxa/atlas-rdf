package uk.ac.ebi.spot.atlas.rdf.loader;

import uk.ac.ebi.atlas.commons.streams.ObjectInputStream;
import uk.ac.ebi.atlas.experimentpage.context.MicroarrayRequestContext;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialProfilesList;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayProfile;
import uk.ac.ebi.atlas.profiles.differential.microarray.MicroarrayProfileStreamFactory;
import uk.ac.ebi.atlas.web.MicroarrayRequestPreferences;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Simon Jupp
 * @date 08/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class MicroarrayProfilesLoader {

    private MicroarrayProfileStreamFactory microarrayProfileStreamFactory;

    @Inject
    public MicroarrayProfilesLoader(MicroarrayProfileStreamFactory microarrayProfileStreamFactory){
        this.microarrayProfileStreamFactory = microarrayProfileStreamFactory;
    }

    public DifferentialProfilesList<MicroarrayProfile> load (MicroarrayExperiment experiment) {
        MicroarrayRequestPreferences preferences = new MicroarrayRequestPreferences();

        // Default values of preferences:
        // preferences.setRegulation(Regulation.UP_DOWN);
        // preferences.setFoldChangeCutoff(1d);
        // preferences.setCutoff(0.05d);

        ObjectInputStream<MicroarrayProfile> stream = microarrayProfileStreamFactory.create(experiment, new MicroarrayRequestContext(preferences, experiment));
        Collection<MicroarrayProfile> profiles = new HashSet<>();
        MicroarrayProfile profile1;
        while ( (profile1 = stream.readNext()) != null) {
            profiles.add(profile1);
        }
        return new DifferentialProfilesList<>(profiles);
    }

}
