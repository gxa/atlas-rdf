package uk.ac.ebi.spot.atlas.rdf.loader;


import uk.ac.ebi.atlas.commons.streams.ObjectInputStream;
import uk.ac.ebi.atlas.experimentpage.context.BaselineRequestContext;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExperiment;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfile;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfilesList;
import uk.ac.ebi.atlas.model.experiment.baseline.Factor;
import uk.ac.ebi.atlas.profiles.ExpressionProfileInputStream;
import uk.ac.ebi.atlas.profiles.baseline.BaselineProfileStreamFactory;
import uk.ac.ebi.atlas.web.BaselineRequestPreferences;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class BaselineProfilesLoader {

    public BaselineProfileStreamFactory getBaselineExpressionsInputStreamFactory() {
        return baselineExpressionsInputStreamFactory;
    }

    @Inject
    public void setBaselineExpressionsInputStreamFactory(BaselineProfileStreamFactory baselineExpressionsInputStreamFactory) {
        this.baselineExpressionsInputStreamFactory = baselineExpressionsInputStreamFactory;
    }

    private BaselineProfileStreamFactory baselineExpressionsInputStreamFactory;


    public BaselineProfilesList load (BaselineExperiment experiment) {
        BaselineRequestPreferences preferences = new BaselineRequestPreferences();
        BaselineRequestContext context = new BaselineRequestContext(preferences, experiment);

//        ExperimentalFactors factors = experiment.getExperimentalFactors();
        Collection<BaselineProfile> profiles = new HashSet<>();

//            for (Factor factor : factors.getAllFactors()) {
                ObjectInputStream<BaselineProfile> stream = getBaselineExpressionsInputStreamFactory().create(experiment, context);

                BaselineProfile profile1;
                while ( (profile1 = stream.readNext()) != null) {
                    profiles.add(profile1);
                }

//            }
        return new BaselineProfilesList(profiles);
    }
}
