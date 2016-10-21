package uk.ac.ebi.spot.atlas.rdf.cache;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.atlas.model.ExperimentType;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutionException;

@Named
public class PublicExperimentTypesCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicExperimentTypesCache.class);

    private LoadingCache<String, ExperimentType> experimentTypes;

    @Inject
    @Named("publicExperimentTypesLoadingCache")
    //this is the name of the implementation being injected, required because LoadingCache is an interface
    public PublicExperimentTypesCache(LoadingCache<String, ExperimentType> experimentTypes) {
        this.experimentTypes = experimentTypes;
    }

    // Throws IllegalStateException if an error was thrown while loading the experiment, or the experiment doesn't exist
    public ExperimentType getExperimentType(String experimentAccession) {
        try {
            return experimentTypes.get(experimentAccession);
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException("Exception while retrieving experiment type: " + e.getMessage(), e.getCause());
        }
    }

    public void evictExperiment(String experimentAccession) {
        experimentTypes.invalidate(experimentAccession);
    }

    public void evictAll() {
        experimentTypes.invalidateAll();
    }

}
