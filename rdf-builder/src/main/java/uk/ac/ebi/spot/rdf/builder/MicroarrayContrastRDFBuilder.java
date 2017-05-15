package uk.ac.ebi.spot.rdf.builder;

import uk.ac.ebi.atlas.model.AssayGroup;
import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.experiment.baseline.Factor;
import uk.ac.ebi.atlas.model.experiment.differential.Contrast;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayExpression;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayProfile;
import uk.ac.ebi.spot.rdf.utils.HashingIdGenerator;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 13/05/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class MicroarrayContrastRDFBuilder extends DifferentialExperimentDesignRDFBuilder<MicroarrayExperiment, GeneProfilesList<MicroarrayProfile>> {

    protected MicroarrayContrastRDFBuilder(URIProvider uriProvider, AssertionBuilder builder) {
        super(uriProvider, builder);
    }

    @Override
    public void build(MicroarrayExperiment experiment, GeneProfilesList<MicroarrayProfile> profile) {
        super.build(experiment, profile);
        buildMicroarrayContrasts(experiment, profile);
    }

    public void buildMicroarrayContrasts(MicroarrayExperiment experiment, GeneProfilesList<MicroarrayProfile> profiles) {
        AssertionBuilder builder = getBuilder();

        for (MicroarrayProfile profile : profiles) {
            // generate triples for each contrast
            String geneId = profile.getId();
            String geneName = profile.getName();
            String designElement = profile.getDesignElementName();

            for (Contrast contrast : profile.getConditions()) {
                URI analysisUri = getAnalaysisUriForContrast(experiment, contrast);
                // create a URI for the expression value based on the accession, contrast id, design element, element id and an incremental number


                MicroarrayExpression expression = profile.getExpression(contrast);

                String frag = HashingIdGenerator.generateHashEncodedID(
                        experiment.getAccession(),
                        analysisUri.toString(),
                        contrast.getArrayDesignAccession(),
                        designElement,
                        String.valueOf(expression.getTstatistic()),
                        String.valueOf(expression.isOverExpressed()),
                        String.valueOf(expression.getPValue()));

                URI diffValueUri = getUriProvider().getExpressionUri(experiment.getAccession(), frag);
                builder.createTypeInstance(
                        diffValueUri,
                        expression.isOverExpressed() ? getUriProvider().getOverExpressedType() : getUriProvider().getUnderExpressedType()
                );

                // get factors
                AssayGroup group = contrast.getTestAssayGroup();
                for (String run : group) {
                    for (Factor factor : experiment.getExperimentDesign().getFactors(run)) {
                        URI experimentalFactorUri = getUriProvider().getFactorUri(experiment.getAccession(), factor.getType() , factor.getValue());
                        builder.createObjectPropertyAssertion(
                                analysisUri,
                                getUriProvider().getExpressionToEfRelUri(),
                                experimentalFactorUri
                        );
                    }
                }


                String regulation = expression.isOverExpressed()? "UP" : "DOWN";
                String label = String.format("%s %s in %s", geneName, regulation, contrast.getDisplayName());
                builder.createLabel(
                        diffValueUri,
                        label
                );

                // this will be a probe id
                URI probeIdUri = getUriProvider().getDesignElementUri(contrast.getArrayDesignAccession(), designElement);
                builder.createTypeInstance(
                        probeIdUri,
                        getUriProvider().getProbeDesignElementType()
                );
                builder.createLabel(
                        probeIdUri,
                        designElement
                );
                builder.createAnnotationAssertion(
                        probeIdUri,
                        getUriProvider().getIdentifierRelUri(),
                        designElement
                );
                // link to platform
                URI platformUri = getUriProvider().getPlatformUri(contrast.getArrayDesignAccession());
                builder.createTypeInstance(
                        platformUri,
                        getUriProvider().getPlatformTypeUri()
                );
                builder.createObjectPropertyAssertion(
                        probeIdUri,
                        getUriProvider().getPartOfPlatfrom(),
                        platformUri
                );
                builder.createLabel(
                        platformUri,
                        contrast.getArrayDesignAccession()
                );
                builder.createAnnotationAssertion(
                        platformUri,
                        getUriProvider().getIdentifierRelUri(),
                        contrast.getArrayDesignAccession()
                );

                // add the link to gene
                for (URI geneidUri : getUriProvider().getBioentityUri(geneId, experiment.getSpecies().getName())) {
                    builder.createTypeInstance(
                            geneidUri,
                            getUriProvider().getBioentityTypeUri("EnsemblDatabaseReference")
                    );
                    builder.createLabel(
                            geneidUri,
                            geneName
                    );
                    builder.createAnnotationAssertion(
                            geneidUri,
                            getUriProvider().getIdentifierRelUri(),
                            geneId
                    );

                    builder.createObjectPropertyAssertion(
                            diffValueUri,
                            getUriProvider().getDiffValueToProbeElementRel(),
                            geneidUri
                    );

                }
                // finally link the expression value to the assay groups and the design element and assert the expression values

                builder.createObjectPropertyAssertion(
                        diffValueUri,
                        getUriProvider().getExpressionValueToAnalysisRelUri(),
                        analysisUri
                );
                builder.createObjectPropertyAssertion(
                        analysisUri,
                        getUriProvider().getAnalysisToExpressionValueRelUri(),
                        diffValueUri
                );
                builder.createObjectPropertyAssertion(
                        diffValueUri,
                        getUriProvider().getDiffValueToProbeElementRel(),
                        probeIdUri
                );

                builder.createDataPropertyAssertion(
                        diffValueUri,
                        getUriProvider().getTStatRelation(),
                        expression.getTstatistic()
                );

                builder.createDataPropertyAssertion(
                        diffValueUri,
                        getUriProvider().getPValueRelation(),
                        expression.getPValue()
                );

                builder.createDataPropertyAssertion(
                        diffValueUri,
                        getUriProvider().getFoldChangeRelation(),
                        expression.getFoldChange()
                );
            }
        }

    }

}
