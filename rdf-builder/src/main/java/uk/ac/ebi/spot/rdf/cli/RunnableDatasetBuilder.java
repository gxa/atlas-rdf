package uk.ac.ebi.spot.rdf.cli;

import uk.ac.ebi.spot.atlas.rdf.ExperimentDTO;
import uk.ac.ebi.spot.rdf.builder.ExperimentToRDFConverter;
import uk.ac.ebi.spot.rdf.builder.URIProvider;
import uk.ac.ebi.spot.atlas.model.CompleteExperiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * @author Simon Jupp
 * @date 13/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class RunnableDatasetBuilder implements Runnable {

    private final String accession;
    private final ExperimentDTO builder;
    private final URIProvider uriProvider;
    private final File outputDir;
    private final String format;

    public RunnableDatasetBuilder(String accession, ExperimentDTO builder, URIProvider uriProvider, String format, File outputDir) {
        this.accession = accession;
        this.builder = builder;
        this.uriProvider = uriProvider;
        this.outputDir = outputDir;
        this.format = format;
    }


    public void run() {
        CompleteExperiment experiment = null;
        try {
            experiment = builder.build(accession);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String suffix = format.equals("TURTLE") ? ".ttl" : ".rdf";
        File out = new File(outputDir.getAbsoluteFile(), experiment.getExperiment().getAccession()+ suffix);


        try {
            FileOutputStream writer = new FileOutputStream(out);
            ExperimentToRDFConverter converter = new ExperimentToRDFConverter(uriProvider);
            converter.convert(experiment.getExperiment(), experiment.getGeneProfilesList(), writer, format);

            builder.build(accession);
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + out.toString());
        } catch (Exception e) {
            System.out.println("Error converting " + accession + e.toString());
        }



    }



}