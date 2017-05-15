package uk.ac.ebi.spot.rdf.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.atlas.model.experiment.Experiment;
import uk.ac.ebi.spot.atlas.rdf.ExperimentDTO;
import uk.ac.ebi.spot.rdf.jena.JenaAssertionBuilder;
import uk.ac.ebi.spot.atlas.model.CompleteExperiment;
import uk.ac.ebi.atlas.model.GeneProfilesList;
import uk.ac.ebi.atlas.model.Profile;

import java.io.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Simon Jupp
 * @date 13/05/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class ExperimentToRDFConverter {

    private URIProvider uriProvider;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public URIProvider getUriProvider() {
        return uriProvider;
    }

    public void setUriProvider(URIProvider uriProvider) {
        this.uriProvider = uriProvider;
    }

    public ExperimentToRDFConverter(URIProvider uriProvider) {

        this.uriProvider = uriProvider;
    }


    public void convert (Experiment experiment, GeneProfilesList<Profile> profile, OutputStream stream, String format) {


        JenaAssertionBuilder jenaAssertionBuilder = new JenaAssertionBuilder();

        ExperimentBuilder builder = null;
        log.info(String.format("Preparing to convert %s with accession %s to RDF", experiment.getType().getDescription(), experiment.getAccession()));

        if (experiment.getType().isMicroarray() ) {
            builder = new MicroarrayContrastRDFBuilder(uriProvider, jenaAssertionBuilder);
        }
        else if (experiment.getType().isDifferential()) {
            builder = new RnaSeqContrastRDFBuilder(uriProvider, jenaAssertionBuilder);
        }
        else if (experiment.getType().isBaseline()) {
            builder = new RnaSeqBaselineRDFBuilder(uriProvider, jenaAssertionBuilder);
        }
        else {
            throw new RuntimeException("Unrecognised experiment type");
        }

        builder.build(experiment, profile);
        builder.flushModel(stream, format);

        log.info(String.format("Finished converting %s to RDF",experiment.getAccession()));

    }

    public static void main(String[] args) throws ExecutionException {

        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        ExperimentDTO builder =context.getBean("experimentBuilder", ExperimentDTO.class);
        URIProvider uriProvider = context.getBean("uriProvider", URIProvider.class);

        //        dto.setLimpopoUtils(utils);
                // micro array
        //       E-GEOD-2210

                // rnaseq diff
        //      E-GEOD-38400

                // baseline
//                E-GEOD-26284

      CompleteExperiment experiment = builder.build("E-GEOD-20523");
//        CompleteExperiment experiment = builder.build("E-GEOD-3307");

        String outfile = "/Users/jupp/dev/gxardf-data/expression-atlas-rdf/" + experiment.getExperiment().getAccession()+ ".rdf";
        try {
            FileOutputStream writer = new FileOutputStream(new File(outfile));
            ExperimentToRDFConverter converter = new ExperimentToRDFConverter(uriProvider);
            converter.convert(experiment.getExperiment(), experiment.getGeneProfilesList(), writer, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
