package uk.ac.ebi.spot.atlas.rdf.profiles.differential.rnaseq;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import uk.ac.ebi.spot.atlas.rdf.commons.ObjectInputStream;
import uk.ac.ebi.spot.atlas.rdf.profiles.DifferentialProfileStreamOptions;
import uk.ac.ebi.spot.atlas.rdf.profiles.ProfileStreamFactory;
import uk.ac.ebi.spot.atlas.rdf.profiles.differential.IsDifferentialExpressionAboveCutOff;
import uk.ac.ebi.spot.atlas.rdf.utils.CsvReaderFactory;
import uk.ac.ebi.spot.rdf.model.differential.Contrast;
import uk.ac.ebi.spot.rdf.model.differential.Regulation;
import uk.ac.ebi.spot.rdf.model.differential.rnaseq.RnaSeqProfile;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;

@Named
@Scope("prototype")
public class RnaSeqProfileStreamFactory implements ProfileStreamFactory<DifferentialProfileStreamOptions,
        RnaSeqProfile, Contrast> {

    @Value("#{configuration['diff.experiment.data.path.template']}")
    private String experimentDataFileUrlTemplate;

    private ExpressionsRowDeserializerRnaSeqBuilder expressionsRowDeserializerRnaSeqBuilder;

    private CsvReaderFactory csvReaderFactory;

    @Inject
    public RnaSeqProfileStreamFactory(ExpressionsRowDeserializerRnaSeqBuilder expressionsRowDeserializerRnaSeqBuilder,
                                      CsvReaderFactory csvReaderFactory) {
        this.expressionsRowDeserializerRnaSeqBuilder = expressionsRowDeserializerRnaSeqBuilder;
        this.csvReaderFactory = csvReaderFactory;
    }

    public ObjectInputStream<RnaSeqProfile> create(DifferentialProfileStreamOptions options) {
        String experimentAccession = options.getExperimentAccession();
        double pValueCutOff = options.getPValueCutOff();
        double foldChangeCutOff = options.getFoldChangeCutOff();
        Regulation regulation = options.getRegulation();

        return create(experimentAccession, pValueCutOff, foldChangeCutOff, regulation);
    }

    public RnaSeqProfilesTsvInputStream create(String experimentAccession, double pValueCutOff, double foldChangeCutOff, Regulation regulation) {
        String tsvFileURL = MessageFormat.format(experimentDataFileUrlTemplate, experimentAccession);
        CSVReader csvReader = csvReaderFactory.createTsvReader(tsvFileURL);

        IsDifferentialExpressionAboveCutOff expressionFilter = new IsDifferentialExpressionAboveCutOff();
        expressionFilter.setPValueCutoff(pValueCutOff);
        expressionFilter.setFoldChangeCutOff(foldChangeCutOff);
        expressionFilter.setRegulation(regulation);

        RnaSeqProfileReusableBuilder rnaSeqProfileReusableBuilder = new RnaSeqProfileReusableBuilder(expressionFilter);

        return new RnaSeqProfilesTsvInputStream(csvReader, experimentAccession, expressionsRowDeserializerRnaSeqBuilder, rnaSeqProfileReusableBuilder);
    }

}
