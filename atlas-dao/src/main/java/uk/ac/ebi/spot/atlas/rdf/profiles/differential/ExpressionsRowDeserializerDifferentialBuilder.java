package uk.ac.ebi.spot.atlas.rdf.profiles.differential;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.spot.atlas.rdf.cache.ExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.profiles.ExpressionsRowDeserializerBuilder;
import uk.ac.ebi.spot.atlas.rdf.profiles.ExpressionsRowTsvDeserializer;
import uk.ac.ebi.atlas.model.Expression;
import uk.ac.ebi.atlas.model.differential.Contrast;
import uk.ac.ebi.atlas.model.differential.DifferentialExperiment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkState;

public abstract class ExpressionsRowDeserializerDifferentialBuilder<T extends Expression, K extends DifferentialExperiment> implements ExpressionsRowDeserializerBuilder<String, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionsRowDeserializerDifferentialBuilder.class);

    private ExperimentsCache<K> experimentsCache;
    private String experimentAccession;
    private List<Contrast> orderedContrasts;

    public ExpressionsRowDeserializerDifferentialBuilder(ExperimentsCache<K> experimentsCache) {
        this.experimentsCache = experimentsCache;
    }

    @Override
    public ExpressionsRowDeserializerDifferentialBuilder<T, K> forExperiment(String experimentAccession) {
        this.experimentAccession = experimentAccession;
        return this;
    }

    @Override
    public ExpressionsRowDeserializerDifferentialBuilder<T, K> withHeaders(String... tsvFileHeaders) {

        try {
            LOGGER.debug("<withHeaders> data file headers: {}", Arrays.toString(tsvFileHeaders));

            checkState(experimentAccession != null, "Builder not properly initialized!");

            DifferentialExperiment experiment = experimentsCache.getExperiment(experimentAccession);

            List<String> columnHeaders = Arrays.asList(tsvFileHeaders);

            orderedContrasts = new LinkedList<>();
            for (String columnHeader : columnHeaders) {
                if (columnHeader.endsWith(".p-value")) {
                    String contrastId = StringUtils.substringBefore(columnHeader, ".");
                    orderedContrasts.add(experiment.getContrast(contrastId));
                }
            }

            return this;
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to load experiment from cache: " + experimentAccession, e);
        }
    }

    @Override
    public ExpressionsRowTsvDeserializer<T> build() {

        checkState(!CollectionUtils.isEmpty(orderedContrasts), "Builder state not ready for creating the ExpressionBuffer");

        return getBufferInstance(orderedContrasts);

    }

    protected abstract ExpressionsRowTsvDeserializer<T> getBufferInstance(List<Contrast> orderedContrasts);
}
