package uk.ac.ebi.spot.atlas.rdf.cache;

import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.atlas.model.baseline.BaselineExperiment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutionException;

@Named
public class ProteomicsBaselineExperimentsCache implements ExperimentsCache<BaselineExperiment> {

    private LoadingCache<String, BaselineExperiment> experiments;

    @Inject
    public ProteomicsBaselineExperimentsCache(@Qualifier("proteomicsBaselineExperimentsLoadingCache") LoadingCache<String, BaselineExperiment> experiments) {
        this.experiments = experiments;
    }

    @Override
    public BaselineExperiment getExperiment(String experimentAccession) throws ExecutionException {
        return experiments.get(experimentAccession);
    }

    @Override
    public void evictExperiment(String experimentAccession) {
        experiments.invalidate(experimentAccession);
    }

    @Override
    public void evictAll() {
        experiments.invalidateAll();
    }

}
