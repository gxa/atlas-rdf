package uk.ac.ebi.spot.rdf.builder;


import uk.ac.ebi.atlas.model.AssayGroup;
import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExperiment;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExpression;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfile;
import uk.ac.ebi.atlas.model.experiment.baseline.Factor;
import uk.ac.ebi.spot.rdf.utils.HashingIdGenerator;

import java.net.URI;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 28/07/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class RnaSeqBaselineRDFBuilder extends AbstractExperimentBuilder<BaselineExperiment, GeneProfilesList<BaselineProfile>> {

    protected RnaSeqBaselineRDFBuilder(URIProvider uriProvider, AssertionBuilder builder) {
        super(uriProvider, builder);
    }

    @Override
    public void build(BaselineExperiment experiment, GeneProfilesList<BaselineProfile> profiles) {
        super.buildExperimentDescription(experiment);
        buildExperimentDesign(experiment);
        buildBaseline(experiment, profiles);
    }

    public void buildBaseline(BaselineExperiment experiment, GeneProfilesList<BaselineProfile> profiles) {

        AssertionBuilder builder = getBuilder();

        // create a map mapping a factor to an assay group
        Map<Factor, Set<AssayGroup>> factorToAssayGroup = new HashMap<>();
        for (AssayGroup assayGroup : experiment.getDataColumnDescriptors()) {
            for (Factor factor : experiment.getFactors(assayGroup)) {
                if (!factorToAssayGroup.containsKey(factor)) {
                    factorToAssayGroup.put(factor, new HashSet<AssayGroup>());
                }
                factorToAssayGroup.get(factor).add(assayGroup);
            }
        }

        // for each factor create a URI to represent the analysis of a group of assays
        Map<Factor, URI> factorToAnalysisUri = new HashMap<>();

        for (Factor factor : factorToAssayGroup.keySet()) {

            Set<AssayGroup> assayGroup = factorToAssayGroup.get(factor);

            String id = HashingIdGenerator.generateHashEncodedID(assayGroup.toString());
            URI analysisURI = getUriProvider().getAnalysisUri(experiment.getAccession(), id);

            if (factorToAnalysisUri.containsKey(factor)) {
                log.error("Factors should be unique for a baseline experiment");
                throw new RuntimeException("Factors should be unique for a baseline experiment");
            }
            factorToAnalysisUri.put(factor, analysisURI);

            builder.createTypeInstance(
                    analysisURI,
                    getUriProvider().getRNASeqBaselineAnalysisTypeUri()
            );

            // link experiment to analysis
            URI experimentUri = getUriProvider().getExperimentUri(experiment.getAccession());
            builder.createObjectPropertyAssertion(
                    experimentUri,
                    getUriProvider().getExperimentToAnalysisRel(),
                    analysisURI
            );


            // get the groups and link to assays,
            for (AssayGroup group : assayGroup) {

                for (String assay : group) {
                    URI assayUri = getUriProvider().getAssayUri(experiment.getAccession(), assay);
                    builder.createObjectPropertyAssertion(
                            analysisURI,
                            getUriProvider().getReferenceAssayRelUri(),
                            assayUri
                    );
                }
            }

            // get the factor
            URI experimentalFactorUri = getUriProvider().getFactorUri(experiment.getAccession(), factor.getType() , factor.getValue());
            builder.createObjectPropertyAssertion(
                    analysisURI,
                    getUriProvider().getExpressionToEfRelUri(),
                    experimentalFactorUri
            );

        }

        for (BaselineProfile profile : profiles) {
            for (AssayGroup assayGroup : profile.getConditions()) {
                BaselineExpression expression = profile.getExpression(assayGroup);
                if (!expression.isKnown()) {
                    continue;
                }

                for (Factor factor : experiment.getFactors(assayGroup)) {

                    String frag = HashingIdGenerator.generateHashEncodedID(
                            experiment.getAccession(),
                            profile.getId(),
                            factorToAnalysisUri.get(factor).toString(),
                            factor.toString(),
                            String.valueOf(expression.getLevel()));

                    URI baselineValueUri = getUriProvider().getExpressionUri(experiment.getAccession(), frag);
                    builder.createTypeInstance(
                            baselineValueUri,
                            getUriProvider().getBaselineExpressionValueType()
                    );
                    builder.createLabel(
                            baselineValueUri,
                            profile.getName() + " expressed in " + factor.getValue()
                    );

                    builder.createObjectPropertyAssertion(
                            factorToAnalysisUri.get(factor),
                            getUriProvider().getAnalysisToExpressionValueRelUri(),
                            baselineValueUri
                    );

                    for (URI geneidUri : getUriProvider().getBioentityUri(profile.getId(), experiment.getSpecies().getName())) {

                        builder.createTypeInstance(
                                geneidUri,
                                getUriProvider().getBioentityTypeUri("EnsemblDatabaseReference")
                        );

                        builder.createLabel(
                                geneidUri,
                                profile.getName()
                        );

                        builder.createAnnotationAssertion(
                                geneidUri,
                                getUriProvider().getIdentifierRelUri(),
                                profile.getId()
                        );

                        builder.createObjectPropertyAssertion(
                                baselineValueUri,
                                getUriProvider().getDiffValueToProbeElementRel(),
                                geneidUri
                        );
                    }

                    buildExpressionForFactor(baselineValueUri, expression);
                }
            }
        }
    }

    public void buildExpressionForFactor (URI baselineExpressionUri, BaselineExpression expression) {
        AssertionBuilder builder = getBuilder();

        builder.createDataPropertyAssertion(
                baselineExpressionUri,
                getUriProvider().getBaselineExpressionLevelRelation(),
                expression.getLevel()
        );
    }
}
