package uk.ac.ebi.spot.rdf.builder;

import uk.ac.ebi.atlas.model.AssayGroup;
import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.experiment.baseline.Factor;
import uk.ac.ebi.atlas.model.experiment.differential.Contrast;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialExpression;
import uk.ac.ebi.atlas.model.experiment.differential.rnaseq.RnaSeqProfile;
import uk.ac.ebi.spot.rdf.utils.HashingIdGenerator;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 28/07/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class RnaSeqContrastRDFBuilder extends DifferentialExperimentDesignRDFBuilder<DifferentialExperiment, GeneProfilesList<RnaSeqProfile>> {


    protected RnaSeqContrastRDFBuilder(URIProvider uriProvider, AssertionBuilder builder) {
        super(uriProvider, builder);
    }

    @Override
    public void build(DifferentialExperiment experiment, GeneProfilesList<RnaSeqProfile> profile) {
        super.build(experiment, profile);
        buildRnaSeqContrasts(experiment, profile);
    }

    public void buildRnaSeqContrasts(DifferentialExperiment experiment, GeneProfilesList<RnaSeqProfile> profiles) {
        AssertionBuilder builder = getBuilder();

        for (RnaSeqProfile profile : profiles) {

            String geneId = profile.getId();
            String geneName = profile.getName();

            // generate triples for each contrast
            for (Contrast contrast : profile.getConditions()) {
                URI analaysisUriForContrast = getAnalaysisUriForContrast(experiment, contrast);

                // create a URI for the expression value based on the accession, contrast id, design element, element id and an incremental number
                DifferentialExpression expression = profile.getExpression(contrast);

                String frag = HashingIdGenerator.generateHashEncodedID(
                        experiment.getAccession(),
                        analaysisUriForContrast.toString(),
                        geneId,
                        contrast.getId(),
                        String.valueOf(expression.getPValue()),
                        String.valueOf(expression.getFoldChange()),
                        String.valueOf(expression.isOverExpressed()));

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
                                analaysisUriForContrast,
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

                // link to ensembl gene
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
                        analaysisUriForContrast
                );
                builder.createObjectPropertyAssertion(
                        analaysisUriForContrast,
                        getUriProvider().getAnalysisToExpressionValueRelUri(),
                        diffValueUri
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
