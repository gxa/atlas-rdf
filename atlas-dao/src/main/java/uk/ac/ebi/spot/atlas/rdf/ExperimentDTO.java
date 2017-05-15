package uk.ac.ebi.spot.atlas.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.atlas.trader.cache.ProteomicsBaselineExperimentsCache;
import uk.ac.ebi.spot.atlas.model.CompleteExperiment;
import uk.ac.ebi.atlas.model.experiment.ExperimentConfiguration;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineExperiment;
import uk.ac.ebi.atlas.model.experiment.baseline.BaselineProfilesList;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.DifferentialProfilesList;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayExperiment;
import uk.ac.ebi.atlas.model.experiment.differential.microarray.MicroarrayProfile;
import uk.ac.ebi.atlas.model.experiment.differential.rnaseq.RnaSeqProfile;
import uk.ac.ebi.atlas.trader.ConfigurationTrader;
import uk.ac.ebi.atlas.trader.cache.MicroarrayExperimentsCache;
import uk.ac.ebi.atlas.trader.cache.RnaSeqBaselineExperimentsCache;
import uk.ac.ebi.atlas.trader.cache.RnaSeqDiffExperimentsCache;
import uk.ac.ebi.spot.atlas.rdf.loader.baseline.ProteomicsBaselineProfilesLoader;
import uk.ac.ebi.spot.atlas.rdf.loader.baseline.RnaSeqBaselineProfilesLoader;
import uk.ac.ebi.spot.atlas.rdf.loader.differential.MicroarrayProfilesLoader;
import uk.ac.ebi.spot.atlas.rdf.loader.differential.RnaSeqDiffProfilesLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutionException;

/**
 * @author Simon Jupp
 * @date 07/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Named("experimentBuilder")
public class ExperimentDTO {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ConfigurationTrader configurationTrader;

    private MicroarrayExperimentsCache microarrayExperimentCache;
    private MicroarrayProfilesLoader microarrayProfilesLoader;

    private RnaSeqDiffExperimentsCache rnaReqDiffExperimentsCache;
    private RnaSeqDiffProfilesLoader rnaSeqDiffProfilesLoader;

    private RnaSeqBaselineExperimentsCache rnaSeqBaselineExperimentsCache;
    private RnaSeqBaselineProfilesLoader rnaSeqBaselineProfilesLoader;

    private ProteomicsBaselineExperimentsCache proteomicsBaselineExperimentsCache;
    private ProteomicsBaselineProfilesLoader proteomicsBaselineProfilesLoader;

    public RnaSeqBaselineExperimentsCache getRnaSeqBaselineExperimentsCache() {
        return rnaSeqBaselineExperimentsCache;
    }

    @Inject
    public void setRnaSeqBaselineExperimentsCache(RnaSeqBaselineExperimentsCache rnaSeqBaselineExperimentsCache) {
        this.rnaSeqBaselineExperimentsCache = rnaSeqBaselineExperimentsCache;
    }

    public RnaSeqBaselineProfilesLoader getRnaSeqBaselineProfilesLoader() {
        return rnaSeqBaselineProfilesLoader;
    }

    @Inject
    public void setRnaSeqBaselineProfilesLoader(RnaSeqBaselineProfilesLoader rnaSeqBaselineProfilesLoader) {
        this.rnaSeqBaselineProfilesLoader = rnaSeqBaselineProfilesLoader;
    }

    public ProteomicsBaselineExperimentsCache getProteomicsBaselineExperimentsCache() {
        return proteomicsBaselineExperimentsCache;
    }

    @Inject
    public void setProteomicsBaselineExperimentsCache(ProteomicsBaselineExperimentsCache proteomicsBaselineExperimentsCache) {
        this.proteomicsBaselineExperimentsCache = proteomicsBaselineExperimentsCache;
    }

    public ProteomicsBaselineProfilesLoader getProteomicsBaselineProfilesLoader() {
        return proteomicsBaselineProfilesLoader;
    }

    @Inject
    public void setProteomicsBaselineProfilesLoader(ProteomicsBaselineProfilesLoader proteomicsBaselineProfilesLoader) {
        this.proteomicsBaselineProfilesLoader = proteomicsBaselineProfilesLoader;
    }

    public RnaSeqDiffExperimentsCache getRnaReqDiffExperimentsCache() {
        return rnaReqDiffExperimentsCache;
    }

    @Inject
    public void setRnaReqDiffExperimentsCache(RnaSeqDiffExperimentsCache rnaReqDiffExperimentsCache) {
        this.rnaReqDiffExperimentsCache = rnaReqDiffExperimentsCache;
    }

    public RnaSeqDiffProfilesLoader getRnaSeqDiffProfilesLoader() {
        return rnaSeqDiffProfilesLoader;
    }

    @Inject
    public void setRnaSeqDiffProfilesLoader(RnaSeqDiffProfilesLoader rnaSeqDiffProfilesLoader) {
        this.rnaSeqDiffProfilesLoader = rnaSeqDiffProfilesLoader;
    }

    public MicroarrayProfilesLoader getMicroarrayProfilesLoader () {
        return microarrayProfilesLoader;
    }

    @Inject
    public void setMicroarrayProfilesLoader (MicroarrayProfilesLoader microarrayProfileLoader) {
        this.microarrayProfilesLoader = microarrayProfileLoader;
    }

    public MicroarrayExperimentsCache getMicroarrayExperimentCache() {
        return microarrayExperimentCache;
    }

    @Inject
    public void setMicroarrayExperimentCache(MicroarrayExperimentsCache microarrayExperimentCache) {
        this.microarrayExperimentCache = microarrayExperimentCache;
    }

    public ConfigurationTrader getConfigurationTrader() {
        return configurationTrader;
    }

    @Inject
    public void setConfigurationTrader(ConfigurationTrader configurationTrader) {
        this.configurationTrader = configurationTrader;
    }

    public CompleteExperiment build (String experimentAccession) throws ExecutionException {

        ExperimentConfiguration config = getConfigurationTrader().getExperimentConfiguration(experimentAccession);

        log.info(String.format("Loading %s with accession %s from filesystem", config.getExperimentType().getDescription(), experimentAccession));
        if (config.getExperimentType().isMicroarray()) {
            final MicroarrayExperiment experiment = getMicroarrayExperimentCache().getExperiment(experimentAccession);
            final DifferentialProfilesList<MicroarrayProfile> profiles =  getMicroarrayProfilesLoader().load((MicroarrayExperiment) experiment);
//            getMicroarrayExperimentCache().evictExperiment(experimentAccession);
            return new CompleteExperiment<MicroarrayExperiment, DifferentialProfilesList<MicroarrayProfile>>() {
                @Override
                public MicroarrayExperiment getExperiment() {
                    return experiment;
                }

                @Override
                public DifferentialProfilesList<MicroarrayProfile> getGeneProfilesList() {
                    return profiles;
                }
            };

        }
        else if (config.getExperimentType().isDifferential()) {
            final DifferentialExperiment experiment = getRnaReqDiffExperimentsCache().getExperiment(experimentAccession);
            final DifferentialProfilesList<RnaSeqProfile> profiles =  getRnaSeqDiffProfilesLoader().load(experiment);
//            getRnaReqDiffExperimentsCache().evictExperiment(experimentAccession);

            return new CompleteExperiment<DifferentialExperiment, DifferentialProfilesList<RnaSeqProfile>>() {
                @Override
                public DifferentialExperiment getExperiment() {
                    return experiment;
                }

                @Override
                public DifferentialProfilesList<RnaSeqProfile> getGeneProfilesList() {
                    return profiles;
                }
            };
        }
        else if (config.getExperimentType().isRnaSeqBaseline()) {
            final BaselineExperiment experiment = getRnaSeqBaselineExperimentsCache().getExperiment(experimentAccession);
            final BaselineProfilesList profiles = getRnaSeqBaselineProfilesLoader().load(experiment);
//            getRnaSeqBaselineExperimentsCache().evictExperiment(experimentAccession);

            return new CompleteExperiment<BaselineExperiment, BaselineProfilesList>() {
                @Override
                public BaselineExperiment getExperiment() {
                    return experiment;
                }

                @Override
                public BaselineProfilesList getGeneProfilesList() {
                    return profiles;
                }
            };
        } else if (config.getExperimentType().isProteomicsBaseline()) {
            final BaselineExperiment experiment = getProteomicsBaselineExperimentsCache().getExperiment(experimentAccession);
            final BaselineProfilesList profiles = getProteomicsBaselineProfilesLoader().load(experiment);
//            getProteomicsBaselineExperimentsCache().evictExperiment(experimentAccession);

            return new CompleteExperiment<BaselineExperiment, BaselineProfilesList>() {
                @Override
                public BaselineExperiment getExperiment() {
                    return experiment;
                }

                @Override
                public BaselineProfilesList getGeneProfilesList() {
                    return profiles;
                }
            };
        }

        throw new RuntimeException("Can't load experiment: " + experimentAccession + ", unrecognised experiment type");

    }

//    private void baselineToString(BaselineExperiment experiment, BaselineProfilesList profiles) {
//        System.out.println(String.format("Experiment accesion: %s", experiment.getAccession()));
//        System.out.println(String.format("Experiment description: %s", experiment.getDescription()));
//        System.out.println(String.format("Experiment type: %s", experiment.getType().getDescription()));
//        System.out.println(String.format("Experiment display name: %s", experiment.getDisplayName()));
//        System.out.println(String.format("Experiment pubmedids: %s", experiment.getAttributes().get("pubMedIds")));
//        System.out.println(String.format("Experiment Species: %s", experiment.getSpecies()));
//        ExperimentDesign design = experiment.getExperimentDesign();
//
//        for (String runs : experiment.getExperimentRunAccessions()) {
//            System.out.println(String.format("Experiment run: %s", runs));
//            for (SampleCharacteristic sample : design.getSampleCharacteristics(runs)) {
//                System.out.println(String.format("\t sample: %s / %s (%s)", sample.header(),  sample.value(), Joiner.on(" ").join(sample.valueOntologyTerms())));
//            }
//            for (Factor factor : design.getFactors(runs)) {
//                System.out.println(String.format("\t factor: %s / %s (%s)", factor.getType(), factor.getValue(), Joiner.on(" ").join(factor.getValueOntologyTerms())));
//            }
//
//        }
//
//        for (BaselineProfile profile : profiles) {
//            for (Factor factor : profile.getConditions()) {
//                if (profile.getExpression(factor).isKnown()) {
//                    System.out.println(String.format("baseline factor: %s %s", factor.getType(), factor.getValue()));
//                    System.out.println(String.format("Expression: %s %s %s",
//                            profile.getId(),
//                            profile.getName(),
//                            profile.getExpression(factor).getLevel()));
//                }
//            }
//        }
//    }

//    public void diffToString(DifferentialExperiment experiment, DifferentialProfilesList<RnaSeqProfile> profiles) {
//        System.out.println(String.format("Experiment accesion: %s", experiment.getAccession()));
//        System.out.println(String.format("Experiment description: %s", experiment.getDescription()));
//        System.out.println(String.format("Experiment type: %s", experiment.getType().getDescription()));
//        System.out.println(String.format("Experiment display name: %s", experiment.getDisplayName()));
//        System.out.println(String.format("Experiment pubmedids: %s", experiment.getAttributes().get("pubMedIds")));
//        System.out.println(String.format("Experiment Species: %s", experiment.getSpecies()));
//        ExperimentDesign design = experiment.getExperimentDesign();
//        for (String assays: experiment.getAssayAccessions()) {
//            System.out.println(String.format("Experiment assays: %s", assays));
//            for (SampleCharacteristic sample : design.getSampleCharacteristics(assays)) {
//                System.out.println(String.format("\t sample: %s / %s (%s)", sample.header(),  sample.value(), Joiner.on(" ").join(sample.valueOntologyTerms())));
//            }
//            for (Factor factor : design.getFactors(assays)) {
//                System.out.println(String.format("\t factor: %s / %s (%s)", factor.getType(), factor.getValue(), Joiner.on(" ").join(factor.getValueOntologyTerms())));
//            }
//        }
//
//
//        for (RnaSeqProfile profile : profiles) {
//            for (Contrast contrast : profile.getConditions()) {
//
//                for (String refGroup : contrast.getReferenceAssayGroup()) {
//                    System.out.println(String.format("Reference assay group: %s", refGroup));
//                }
//                for (String control : contrast.getTestAssayGroup()) {
//                    System.out.println(String.format("Test assay group: %s", control));
//                }
//
//                System.out.println(String.format("Expression: %s %s %s %s %s",
//                        profile.getId(),
//                        profile.getName(),
//                        profile.getExpression(contrast).getPValue(),
//                        profile.getExpression(contrast).getFoldChange(),
//                        profile.getExpression(contrast).isOverExpressed() ? "UP" : "DOWN"));
//            }
//        }
//    }

//    public void microArrayToString(MicroarrayExperiment experiment, DifferentialProfilesList<MicroarrayProfile> profiles) {
//        System.out.println(String.format("Experiment accesion: %s", experiment.getAccession()));
//        System.out.println(String.format("Experiment description: %s", experiment.getDescription()));
//        System.out.println(String.format("Experiment type: %s", experiment.getType().getDescription()));
//        System.out.println(String.format("Experiment display name: %s", experiment.getDisplayName()));
//        System.out.println(String.format("Experiment pubmedids: %s", experiment.getAttributes().get("pubMedIds")));
//        System.out.println(String.format("Experiment Species: %s", experiment.getSpecies()));
//        ExperimentDesign design = experiment.getExperimentDesign();
//        for (String assays: experiment.getAssayAccessions()) {
//            System.out.println(String.format("Experiment assays: %s", assays));
//            for (SampleCharacteristic sample : design.getSampleCharacteristics(assays)) {
//                System.out.println(String.format("\t sample: %s / %s (%s)", sample.header(),  sample.value(), Joiner.on(" ").join(sample.valueOntologyTerms())));
//            }
//            for (Factor factor : design.getFactors(assays)) {
//                System.out.println(String.format("\t factor: %s / %s (%s)", factor.getType(), factor.getValue(), Joiner.on(" ").join(factor.getValueOntologyTerms())));
//            }
//        }
//
//
//        for (MicroarrayProfile profile : profiles) {
//            for (Contrast contrast : profile.getConditions()) {
//                System.out.println(String.format("Contrast for array design: %s", contrast.getArrayDesignAccession()));
//
//                for (String refGroup : contrast.getReferenceAssayGroup()) {
//                    System.out.println(String.format("Reference assay group: %s", refGroup));
//                }
//                for (String control : contrast.getTestAssayGroup()) {
//                    System.out.println(String.format("Test assay group: %s", control));
//                }
//
//                System.out.println(String.format("Expression: %s %s %s %s %s %s %s",
//                        profile.getId(),
//                        profile.getName(),
//                        profile.getDesignElementName(),
//                        profile.getExpression(contrast).getTstatistic(),
//                        profile.getExpression(contrast).getPValue(),
//                        profile.getExpression(contrast).getFoldChange(),
//                        profile.getExpression(contrast).isOverExpressed() ? "UP" : "DOWN"));
//            }
//
//        }
//    }

    public static void main(String[] args) throws ExecutionException {

        ApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");


        ConfigurationTrader trader = (ConfigurationTrader) context.getBean("configurationTrader");
        MicroarrayExperimentsCache experimentsCache = (MicroarrayExperimentsCache) context.getBean("microarrayExperimentsCache");
        MicroarrayProfilesLoader maprofile = (MicroarrayProfilesLoader) context.getBean("microarrayProfilesLoader");

        RnaSeqDiffExperimentsCache rnaReqDiffexperimentsCache = (RnaSeqDiffExperimentsCache) context.getBean("rnaSeqDiffExperimentsCache");
        RnaSeqDiffProfilesLoader rnadiffprofile = (RnaSeqDiffProfilesLoader) context.getBean("rnaSeqDiffProfilesLoader");

        RnaSeqBaselineExperimentsCache baselineExperimentsCache = (RnaSeqBaselineExperimentsCache) context.getBean("rnaSeqBaselineExperimentsCache");
        RnaSeqBaselineProfilesLoader baselineProfilesLoader= (RnaSeqBaselineProfilesLoader) context.getBean("rnaSeqBaselineProfilesLoader");

        ExperimentDTO dto = new ExperimentDTO();
        dto.setConfigurationTrader(trader);

        dto.setMicroarrayExperimentCache(experimentsCache);
        dto.setMicroarrayProfilesLoader(maprofile);
        dto.setRnaReqDiffExperimentsCache(rnaReqDiffexperimentsCache);
        dto.setRnaSeqDiffProfilesLoader(rnadiffprofile);
        dto.setRnaSeqBaselineExperimentsCache(baselineExperimentsCache);
        dto.setRnaSeqBaselineProfilesLoader(baselineProfilesLoader);


//        dto.setLimpopoUtils(utils);
        // micro array
//        dto.build("E-GEOD-2210");

        // rnaseq diff
//        dto.build("E-GEOD-38400");

        // baseline
        dto.build("E-GEOD-26284");
    }


}
