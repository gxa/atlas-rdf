package uk.ac.ebi.spot.rdf.builder;

import uk.ac.ebi.atlas.model.AssayGroup;
import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.experiment.differential.Contrast;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialExperiment;
import uk.ac.ebi.spot.rdf.utils.HashingIdGenerator;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 28/07/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public abstract class DifferentialExperimentDesignRDFBuilder<T extends DifferentialExperiment, V extends GeneProfilesList> extends AbstractExperimentBuilder<T, V> {


    protected DifferentialExperimentDesignRDFBuilder(URIProvider uriProvider, AssertionBuilder builder) {
        super(uriProvider, builder);
    }

    public void build(T experiment, V profile) {
        buildExperimentDescription(experiment);
        buildExperimentDesign(experiment);
    }




    public URI getAnalaysisUriForContrast(T experiment, Contrast contrast) {
        AssertionBuilder builder = getBuilder();

        // create a URI based on the ref and test group details
        AssayGroup referenceAssayGroup = contrast.getReferenceAssayGroup();
        AssayGroup testAssayGroup = contrast.getTestAssayGroup();

        String id = HashingIdGenerator.generateHashEncodedID(referenceAssayGroup.toString(), testAssayGroup.toString(), contrast.getArrayDesignAccession());
        URI analysisURI = getUriProvider().getAnalysisUri(experiment.getAccession(), id);

        String label = "Analysis of " + contrast.getDisplayName();
        getBuilder().createLabel(analysisURI, label);

        // default analysis type
        URI type = null;
        if (experiment.getType().isMicroarray()) {
            type = getUriProvider().getMicrorrayAnalysisTypeUri();

        }
        else if (experiment.getType().isDifferential()) {
            type = getUriProvider().getRNASeqDiffAnalysisTypeUri();
        }
        else  {
            type = URI.create("http://rdf.ebi.ac.uk/terms/expressionatlas/Analysis");
            log.warn("No analysis type for differential experiment " + experiment.getAccession());
        }

        builder.createTypeInstance(
                analysisURI,
                type
        );

        for (String assay : referenceAssayGroup) {
            URI assayUri = getUriProvider().getAssayUri(experiment.getAccession(), assay);
            builder.createObjectPropertyAssertion(
                    analysisURI,
                    getUriProvider().getReferenceAssayRelUri(),
                    assayUri
            );
        }
        for (String assay : testAssayGroup) {
            URI assayUri = getUriProvider().getAssayUri(experiment.getAccession(), assay);
            builder.createObjectPropertyAssertion(
                    analysisURI,
                    getUriProvider().getTestAssayRelUri(),
                    assayUri
            );
        }

        // link experiment to analysis

        URI experimentUri = getUriProvider().getExperimentUri(experiment.getAccession());
        builder.createObjectPropertyAssertion(
                experimentUri,
                getUriProvider().getExperimentToAnalysisRel(),
                analysisURI
        );

        return analysisURI;
    }
}
