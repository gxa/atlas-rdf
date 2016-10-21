package uk.ac.ebi.spot.atlas.rdf;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import uk.ac.ebi.spot.atlas.rdf.commons.readers.FileTsvReaderBuilder;
import uk.ac.ebi.spot.atlas.rdf.commons.readers.TsvReader;
import uk.ac.ebi.atlas.model.ExperimentDesign;
import uk.ac.ebi.atlas.model.OntologyTerm;
import uk.ac.ebi.atlas.model.SampleCharacteristic;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//ToDo: (N) to be removed <- (B) and replaced with what??
@Named
@Scope("prototype")
public class ExperimentDesignParser {

    private static final String ONTOLOGY_TERM_DELIMITER = " ";

    static final Pattern SAMPLE_COLUMN_HEADER_PATTERN = Pattern.compile("\\s*Sample Characteristic\\[(.*?)\\]\\s*");
    static final Pattern SAMPLE_ONTOLOGY_TERM_COLUMN_HEADER_PATTERN = Pattern.compile("\\s*Sample Characteristic Ontology Term\\[(.*?)\\]\\s*");

    static final Pattern FACTOR_COLUMN_HEADER_PATTERN = Pattern.compile("\\s*Factor Value\\[(.*?)\\]\\s*");
    static final Pattern FACTOR_VALUE_ONTOLOGY_TERM_COLUMN_HEADER_PATTERN = Pattern.compile("\\s*Factor Value Ontology Term\\[(.*?)\\]\\s*");

    @Value("#{configuration['experiment.experiment-design.path.template']}")
    private String pathTemplate;

    private FileTsvReaderBuilder fileTsvReaderBuilder;

    @Inject
    void setFileTsvReaderBuilder(FileTsvReaderBuilder fileTsvReaderBuilder) {
        this.fileTsvReaderBuilder = fileTsvReaderBuilder;
    }


    public ExperimentDesign parse(String experimentAccession) {

        TsvReader tsvReader = fileTsvReaderBuilder.forTsvFilePathTemplate(pathTemplate)
                                              .withExperimentAccession(experimentAccession)
                                              .build();

        List<String[]> csvLines = new ArrayList<>(tsvReader.readAll());

        if (csvLines.isEmpty()) {
            String tsvFilePath = MessageFormat.format(pathTemplate, experimentAccession);
            throw new IllegalStateException(String.format("%s is empty", tsvFilePath));
        }

        String[] headerLine = csvLines.remove(0);

        Map<String, Integer> sampleHeaderIndexes = extractHeaderIndexes(headerLine, SAMPLE_COLUMN_HEADER_PATTERN);
        Map<String, Integer> sampleValueOntologyTermHeaderIndexes = extractHeaderIndexes(headerLine, SAMPLE_ONTOLOGY_TERM_COLUMN_HEADER_PATTERN);

        Map<String, Integer> factorHeaderIndexes = extractHeaderIndexes(headerLine, FACTOR_COLUMN_HEADER_PATTERN);
        Map<String, Integer> factorValueOntologyTermHeaderIndexes = extractHeaderIndexes(headerLine, FACTOR_VALUE_ONTOLOGY_TERM_COLUMN_HEADER_PATTERN);

        int headersStartIndex = headerLine.length - (sampleHeaderIndexes.size() + sampleValueOntologyTermHeaderIndexes.size() + factorHeaderIndexes.size() + factorValueOntologyTermHeaderIndexes.size());

        ExperimentDesign experimentDesign = new ExperimentDesign();
        for (int i = 0; i < headersStartIndex; i++) {
            experimentDesign.addAssayHeader(headerLine[i]);
        }

        for (String[] line : csvLines) {
            String runOrAssay = line[0];
            if (headersStartIndex > 1) {
                experimentDesign.putArrayDesign(runOrAssay, line[1]);
            }

            for (String sampleHeader : sampleHeaderIndexes.keySet()) {
                String sampleValue = line[sampleHeaderIndexes.get(sampleHeader)];

                Integer sampleValueOntologyTermIndex = sampleValueOntologyTermHeaderIndexes.get(sampleHeader);
                OntologyTerm[] sampleValueOntologyTerms = createOntologyTerms(line, sampleValueOntologyTermIndex);
                SampleCharacteristic sampleCharacteristic = SampleCharacteristic.create(sampleHeader, sampleValue, sampleValueOntologyTerms);

                experimentDesign.putSampleCharacteristic(runOrAssay, sampleHeader, sampleCharacteristic);
            }

            for (String factorHeader : factorHeaderIndexes.keySet()) {
                String factorValue = line[factorHeaderIndexes.get(factorHeader)];

                Integer factorValueOntologyTermIndex = factorValueOntologyTermHeaderIndexes.get(factorHeader);
                OntologyTerm[] factorValueOntologyTerms = createOntologyTerms(line, factorValueOntologyTermIndex);

                experimentDesign.putFactor(runOrAssay, factorHeader, factorValue, factorValueOntologyTerms);
            }
        }

        return experimentDesign;
    }

    private OntologyTerm[] createOntologyTerms(String[] line, Integer ontologyTermIndex) {
        if (ontologyTermIndex == null || line[ontologyTermIndex].isEmpty()) {
            return new OntologyTerm[0];
        }

        ImmutableList.Builder<OntologyTerm> ontologyTermBuilder = new ImmutableList.Builder<>();
        String uriField = line[ontologyTermIndex];
        for (String uri : uriField.split(ONTOLOGY_TERM_DELIMITER)) {
            ontologyTermBuilder.add(OntologyTerm.createFromURI(uri));
        }
        List<OntologyTerm> ontologyTermList = ontologyTermBuilder.build();

        return ontologyTermList.toArray(new OntologyTerm[ontologyTermList.size()]);
    }

    protected Map<String, Integer> extractHeaderIndexes(String[] columnHeaders, Pattern columnHeaderPattern) {
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < columnHeaders.length; i++) {
            String matchingHeaderContent = extractMatchingContent(columnHeaders[i], columnHeaderPattern);
            if (matchingHeaderContent != null) {
                map.put(matchingHeaderContent, i);
            }
        }
        return map;
    }

    static String extractMatchingContent(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

}
