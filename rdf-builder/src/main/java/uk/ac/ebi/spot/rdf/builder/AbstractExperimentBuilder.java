package uk.ac.ebi.spot.rdf.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.atlas.model.experiment.Experiment;
import uk.ac.ebi.atlas.model.experiment.ExperimentDesign;
import uk.ac.ebi.atlas.model.experiment.baseline.Factor;
import uk.ac.ebi.atlas.model.experiment.baseline.impl.FactorSet;
import uk.ac.ebi.spot.rdf.exception.UnknownOrganismTypeException;
import uk.ac.ebi.atlas.model.SampleCharacteristic;
import uk.ac.ebi.atlas.model.GeneProfilesList;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 02/05/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public abstract class AbstractExperimentBuilder <T extends Experiment, V extends GeneProfilesList> implements ExperimentBuilder<T,V>  {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected AbstractExperimentBuilder(URIProvider uriProvider, AssertionBuilder builder) {
        this.uriProvider = uriProvider;
        this.builder = builder;
    }

    private URIProvider uriProvider;
    private AssertionBuilder builder;

    public URIProvider getUriProvider() {
        return uriProvider;
    }

    public AssertionBuilder getBuilder() {
        return builder;
    }

    public void flushModel(OutputStream stream, String format) {
        getBuilder().renderToStream(stream, format);
    }

    public void buildExperimentDesign(T experiment) {
        AssertionBuilder builder = getBuilder();
        String accession = experiment.getAccession();
        URI experimentUri = getUriProvider().getExperimentUri(accession);

        ExperimentDesign experimentDesign = experiment.getExperimentDesign();

        for (String runOrAssay : experimentDesign.getAllRunOrAssay()) {

            URI runUri = getUriProvider().getAssayUri(accession, runOrAssay);
            builder.createObjectPropertyAssertion(experimentUri, getUriProvider().getExperimentToAssayRel(), runUri);
            // type it
            builder.createTypeInstance(runUri, getUriProvider().getAssayType());
            builder.createLabel(runUri, "(Assay) " + runOrAssay);
            builder.createAnnotationAssertion(runUri, getUriProvider().getIdentifierRelUri(), runOrAssay);

            // assert array design if applicable
            String arrayDesign = experimentDesign.getArrayDesign(runOrAssay);
            if (arrayDesign != null) {
                builder.createAnnotationAssertion(runUri, getUriProvider().getParticipantRelUri(), getUriProvider().getPlatformUri(arrayDesign));
            }

            // assert factor values
            FactorSet factorSet = experimentDesign.getFactors(runOrAssay);
            if (factorSet != null) {
                for (Factor factor: factorSet) {
                    URI experimentalFactorUri = getUriProvider().getFactorUri(experiment.getAccession(), factor.getType(),  factor.getValue());
                    builder.createAnnotationAssertion(experimentalFactorUri, getUriProvider().getPropertyTypeAnnotationProperty(), factor.getType());
                    builder.createAnnotationAssertion(experimentalFactorUri, getUriProvider().getPropertyValueAnnotationProperty(), factor.getValue());
                    builder.createLabel(experimentalFactorUri, "(Factor value) " + factor.getType() + "/" + factor.getValue());

                    // todo only one ontology term possible?
                    if (!factor.getValueOntologyTerms().isEmpty()) {
                        builder.createTypeInstance(
                                experimentalFactorUri,
                                getUriProvider().getEfoUriFromFragment(factor.getValueOntologyTerms().iterator().next().uri())
                        );
                    }
                    else {
                        builder.createTypeInstance(
                                experimentalFactorUri,
                                URI.create("http://www.ebi.ac.uk/efo/EFO_0000001")
                        );
                    }

                    builder.createObjectPropertyAssertion(
                            runUri,
                            getUriProvider().getAssayToEfRel(),
                            experimentalFactorUri
                    );
                }
            }

            // sample info
            for (SampleCharacteristic sample : experimentDesign.getSampleCharacteristics(runOrAssay)) {

                URI sampleUri = getUriProvider().getSampleUri(accession, sample.header(),  sample.value());
                builder.createAnnotationAssertion(sampleUri, getUriProvider().getPropertyTypeAnnotationProperty(), sample.header());
                builder.createAnnotationAssertion(sampleUri, getUriProvider().getPropertyValueAnnotationProperty(), sample.value());
                builder.createLabel(sampleUri, "(Sample) " + sample.header() + "/" + sample.value());
                if (!sample.valueOntologyTerms().isEmpty()) {
                    builder.createTypeInstance(
                            sampleUri,
                            getUriProvider().getEfoUriFromFragment(sample.valueOntologyTerms().iterator().next().uri())
                    );
                }
                else {
                    builder.createTypeInstance(
                            sampleUri,
                            URI.create("http://www.ebi.ac.uk/efo/EFO_0000001")
                    );
                }
                builder.createObjectPropertyAssertion(
                        runUri,
                        getUriProvider().getAssayToSampleRel(),
                        sampleUri
                );
            }
        }
    }

    public void buildExperimentDescription (Experiment experiment) {

        String accession = experiment.getAccession();

        URI experimentUri = getUriProvider().getExperimentUri(accession);
        builder.createTypeInstance(
                experimentUri,
                getUriProvider().getExperimentType(experiment.getType().getDescription()));

        builder.createAnnotationAssertion(
                experimentUri,
                getUriProvider().getIdentifierRelUri(),
                accession);

        builder.createAnnotationAssertion(
                experimentUri,
                getUriProvider().getAboutPageRelUri(),
                getUriProvider().getAtlasExperimentUrl(accession));

        builder.createAnnotationAssertion(
                experimentUri, getUriProvider().getDescriptionRelUri(),
                experiment.getDescription());

        if (experiment.getAttributes().get("pubmedIds") == null) {
            log.warn(String.format("Experiment %s has no pubmed IDs", experiment.getAccession()));
        } else {
            for (String pubmedId : (List<String>) experiment.getAttributes().get("pubmedIds")) {
                builder.createAnnotationAssertion(
                        experimentUri,
                        getUriProvider().getPubmedPredicateUri(),
                        getUriProvider().getPubmedUri(pubmedId));
            }
        }

//        builder.createAnnotationAssertion(
//                experimentUri,
//                getUriProvider().getDateOfLastUpdateUri(),
//                experiment.getLastUpdate());

            try {
                URI speciesUri = null;
                speciesUri = getUriProvider().getOrganismUri(experiment.getSpecies().getName());
                builder.createObjectPropertyAssertion(
                        experimentUri,
                        getUriProvider().getTaxonRelUri(),
                        speciesUri);
                builder.createTypeInstance(
                        speciesUri,
                        getUriProvider().getOrganismTypeUri()
                );
                builder.createLabel(speciesUri, experiment.getSpecies().getName());
            } catch (UnknownOrganismTypeException e) {
                log.error("Unknown organism in " + experiment.getAccession() + " " + experiment.getSpecies().getName());
            }

        }
    }

