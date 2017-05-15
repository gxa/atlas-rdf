package uk.ac.ebi.spot.rdf.cli;

import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.spot.atlas.rdf.ExperimentDTO;
import uk.ac.ebi.spot.rdf.builder.ExperimentToRDFConverter;
import uk.ac.ebi.spot.rdf.builder.URIProvider;
import uk.ac.ebi.spot.atlas.model.CompleteExperiment;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simon Jupp
 * @date 13/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class Gxa2RdfDriver {

    private static Set<String> experimentAccession = new HashSet<String>();

    private static File outputDir;
    private static File inputFile;

    private static String format = "TURTLE";

    private static int parseArguments(String[] args) {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.hasOption("") || cl.hasOption("h")) {
                // print out mode help
                help.printHelp("atlas2rdf.sh", options, true);
                parseArgs += 1;
            }
            else {


                // find -o option (for asserted output file)
                if (cl.hasOption("in") ) {
                    inputFile = new File (cl.getOptionValue("in"));

                }
                else {
                    inputFile = new File(".");
                }

                if (inputFile.exists()) {

                    if (cl.hasOption("acc") ) {
                        experimentAccession.add(cl.getOptionValue("acc"));

                    }
                    else {
                        experimentAccession.addAll(Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8));

//                        File[] experimentFiles = inputFile.listFiles(new FilenameFilter() {
//                            @Override
//                            public boolean accept(File dir, String name) {
//                                return name.startsWith("E-");
//                            }
//                        });
//                        for (File experimentFile : experimentFiles) {
//                            experimentAccession.add(experimentFile.getName());
//                        }
                    }

                }
                else {
                    System.err.println("Can't find input directory " + inputFile.toString());
                    System.exit(1);
                }


                if  (cl.hasOption("f")) {
                    format = cl.getOptionValue("f");
                }
                if (cl.hasOption("out"))  {
                    outputDir = new File(cl.getOptionValue("out"));
                }
                else {
                    outputDir = new File(System.getProperty("user.dir"));
                }


            }
        }
        catch (ParseException | IOException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);
            parseArgs += 4;
        }
        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // add output file arguments
        Option experimentAccessionOption = new Option("acc", "accession", true,
                "The atlas experiment accession to convert to OWL/RDF ");
        experimentAccessionOption.setRequired(false);
        options.addOption(experimentAccessionOption);

        Option outputFileOption = new Option("out", "output", true,
                "The output directory write the experiment as RDF is written to");
        outputFileOption.setArgName("directory");
        outputFileOption.setRequired(false);
        options.addOption(outputFileOption);

        Option inputFileOption = new Option("in", "input", true,
                "Input file containing list of accessions to parse, one per line");
        inputFileOption.setArgName("file");
        inputFileOption.setRequired(false);
        options.addOption(inputFileOption);

        Option formatOption = new Option("f", "format", true,
                "The RDF output format, TURTLE or RDFXML");
        formatOption.setArgName("file");
        formatOption.setRequired(false);
        options.addOption(formatOption);

        return options;
    }


    public static void main(String[] args) throws ExecutionException {


        int parseArgs = parseArguments(args);

        if (parseArgs == 0) {

            System.setProperty("data.files.location", inputFile.getAbsolutePath());

            ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");

            ExperimentDTO builder = context.getBean("experimentBuilder", ExperimentDTO.class);
            URIProvider uriProvider = context.getBean("uriProvider", URIProvider.class);


            ExecutorService executor = Executors.newFixedThreadPool(32);

            Set<String> failed = new HashSet<>();
            int x = 1;
            for (String accession : experimentAccession) {
                System.out.println("Building experiment: " + accession + ":" + x + " of " + experimentAccession.size());

                try {
                    CompleteExperiment experiment = builder.build(accession);

                    String suffix = format.equals("TURTLE") ? ".ttl" : ".rdf";
                    File out = new File(outputDir.getAbsoluteFile(), experiment.getExperiment().getAccession()+ suffix);


                        FileOutputStream writer = new FileOutputStream(out);
                        ExperimentToRDFConverter converter = new ExperimentToRDFConverter(uriProvider);
                        converter.convert(experiment.getExperiment(), experiment.getGeneProfilesList(), writer, format);

                        builder.build(accession);

                }
                catch (IllegalStateException e) {
                    System.out.println("Error converting " + accession + e.toString());
                    failed.add(accession);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found " + outputDir.toString() + "accession");
                }

//                Runnable worker = new RunnableDatasetBuilder(s, builder, uriProvider, format, outputDir);
//                executor.execute(worker);
                x++;


            }
            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
//            executor.shutdown();
////            Wait until all threads are finish
//            while (!executor.isTerminated()) {
//
//            }
//            System.out.println("Finished all threads");

            System.out.println("Failed experiments: " + failed.toString());

        }
        else {
            // could not parse arguments, exit with exit code >1 (depending on parsing problem)
            System.err.println("Failed to parse supplied arguments");
            System.exit(1 + parseArgs);
        }
    }

    public static File createOutputDir(File path, String name) {
        File out = new File(path.getAbsolutePath(), name);
        System.out.println("Creating folder for output: " + out.getAbsolutePath());
        out.mkdir();
        return out;

    }

}



