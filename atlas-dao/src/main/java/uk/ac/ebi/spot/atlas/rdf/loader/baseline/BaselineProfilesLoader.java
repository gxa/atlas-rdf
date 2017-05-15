package uk.ac.ebi.spot.atlas.rdf.loader.baseline;

import uk.ac.ebi.atlas.commons.streams.ObjectInputStream;
import uk.ac.ebi.atlas.experimentpage.context.BaselineRequestContext;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExperiment;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfile;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfilesList;
import uk.ac.ebi.atlas.profiles.baseline.BaselineProfileStreamFactory;
import uk.ac.ebi.atlas.web.BaselineRequestPreferences;

import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Alfonso Muñoz-Pomer Fuentes <amunoz@ebi.ac.uk> on 15/05/2017.
 */
public class BaselineProfilesLoader {

    private final BaselineProfileStreamFactory baselineProfileStreamFactory;

    public BaselineProfilesLoader(BaselineProfileStreamFactory baselineProfileStreamFactory) {
        this.baselineProfileStreamFactory = baselineProfileStreamFactory;
    }

    public BaselineProfilesList load (BaselineExperiment experiment) {
        BaselineRequestPreferences preferences = new BaselineRequestPreferences();
        BaselineRequestContext context = new BaselineRequestContext(preferences, experiment);

//        ExperimentalFactors factors = experiment.getExperimentalFactors();
        Collection<BaselineProfile> profiles = new HashSet<>();

//            for (Factor factor : factors.getAllFactors()) {
        ObjectInputStream<BaselineProfile> stream = baselineProfileStreamFactory.create(experiment, context);

        BaselineProfile profile1;
        while ( (profile1 = stream.readNext()) != null) {
            profiles.add(profile1);
        }

//            }
        return new BaselineProfilesList(profiles);
    }

}
