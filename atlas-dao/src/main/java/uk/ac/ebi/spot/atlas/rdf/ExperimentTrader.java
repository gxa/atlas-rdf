package uk.ac.ebi.spot.atlas.rdf;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.atlas.rdf.cache.ExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.cache.MicroarrayExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.cache.ProteomicsBaselineExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.cache.PublicExperimentTypesCache;
import uk.ac.ebi.spot.atlas.rdf.cache.RnaSeqBaselineExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.cache.RnaSeqDiffExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.experimentimport.AtlasExperimentDTO;
import uk.ac.ebi.spot.atlas.rdf.experimentimport.ExperimentDAO;
import uk.ac.ebi.atlas.model.Experiment;
import uk.ac.ebi.atlas.model.ExperimentType;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Named
public class ExperimentTrader {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExperimentTrader.class);
    private ExperimentDAO experimentDAO;
    private PublicExperimentTypesCache publicExperimentTypesCache;
    ImmutableMap<ExperimentType, ExperimentsCache<? extends Experiment>> experimentCachesPerType;


    @Inject
    public ExperimentTrader(ExperimentDAO experimentDAO,
                            RnaSeqBaselineExperimentsCache rnaSeqBaselineExperimentsCache,
                            RnaSeqDiffExperimentsCache rnaSeqDiffExperimentsCache,
                            MicroarrayExperimentsCache microarrayExperimentsCache,
                            ProteomicsBaselineExperimentsCache proteomicsBaselineExperimentsCache,
                            PublicExperimentTypesCache publicExperimentTypesCache) {

        this.experimentDAO = experimentDAO;
        this.publicExperimentTypesCache = publicExperimentTypesCache;
        ImmutableMap.Builder b = ImmutableMap.<ExperimentType, ExperimentsCache<? extends Experiment>>builder()
                .put(ExperimentType.RNASEQ_MRNA_BASELINE,rnaSeqBaselineExperimentsCache)
                .put(ExperimentType.PROTEOMICS_BASELINE,proteomicsBaselineExperimentsCache)
                .put(ExperimentType.RNASEQ_MRNA_DIFFERENTIAL, rnaSeqDiffExperimentsCache);

        for(ExperimentType t: ExperimentType.values()){
            if(t.isMicroarray()){
                b.put(t, microarrayExperimentsCache);
            }
        }
        experimentCachesPerType = b.build();
    }


    public Experiment getPublicExperiment(String experimentAccession) {
        ExperimentType experimentType;
        try {
            experimentType = publicExperimentTypesCache.getExperimentType(experimentAccession);
        } catch (UncheckedExecutionException | NullPointerException e) {
            LOGGER.error(e.getMessage());
            throw new ResourceNotFoundException("Experiment: " + experimentAccession + " not found");
        }

        return getExperimentFromCache(experimentAccession, experimentType);
    }


    public Experiment getExperiment(String experimentAccession, String accessKey) {
        if (StringUtils.isBlank(accessKey)){
            return getPublicExperiment(experimentAccession);
        }
        AtlasExperimentDTO experimentDTO = experimentDAO.findExperiment(experimentAccession, accessKey);

        return getExperimentFromCache(experimentAccession, experimentDTO.getExperimentType());
    }


    public void removeExperimentFromCache(String experimentAccession) {
        for(ExperimentsCache cache: experimentCachesPerType.values()){
            cache.evictExperiment(experimentAccession);
        }
        publicExperimentTypesCache.evictExperiment(experimentAccession);
    }


    public void removeAllExperimentsFromCache() {
        for(ExperimentsCache cache: experimentCachesPerType.values()){
            cache.evictAll();
        }
        publicExperimentTypesCache.evictAll();
    }


    public Experiment getExperimentFromCache(String experimentAccession, ExperimentType experimentType) {
        try {
            return experimentCachesPerType.get(experimentType).getExperiment(experimentAccession);
        } catch (ExecutionException | UncheckedExecutionException e) {
            throw new IllegalStateException("Failed to load experiment from cache: " + experimentAccession, e);
        }
    }

    public Set<String> getBaselineExperimentAccessions() {
        return getPublicExperimentAccessions(ExperimentType.RNASEQ_MRNA_BASELINE);
    }


    public Set<String> getProteomicsBaselineExperimentAccessions() {
        return getPublicExperimentAccessions(ExperimentType.PROTEOMICS_BASELINE);
    }


    public Set<String> getAllBaselineExperimentAccessions() {
        return ImmutableSet.<String>builder().
                addAll(getBaselineExperimentAccessions()).
                addAll(getProteomicsBaselineExperimentAccessions()).
                build();

    }


    public Set<String> getRnaSeqDifferentialExperimentAccessions() {
        return getPublicExperimentAccessions(ExperimentType.RNASEQ_MRNA_DIFFERENTIAL);
    }


    public Set<String> getMicroarrayExperimentAccessions() {
        Set<String> identifiers = Sets.newHashSet();
        identifiers.addAll(getPublicExperimentAccessions(ExperimentType.MICROARRAY_1COLOUR_MRNA_DIFFERENTIAL));
        // as two colour is a subtype of micro array, they need to be added here
        identifiers.addAll((getPublicExperimentAccessions(ExperimentType.MICROARRAY_2COLOUR_MRNA_DIFFERENTIAL)));
        identifiers.addAll((getPublicExperimentAccessions(ExperimentType.MICROARRAY_1COLOUR_MICRORNA_DIFFERENTIAL)));
        return identifiers;
    }


    public Set<String> getPublicExperimentAccessions(ExperimentType... experimentType) {
        return experimentDAO.findPublicExperimentAccessions(experimentType.length == 0 ? ExperimentType.values() : experimentType);
    }

}
