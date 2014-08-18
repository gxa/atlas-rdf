package uk.ac.ebi.spot.atlas.rdf.utils;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Simon Jupp
 * @date 07/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class CsvReaderFactory {

    private static final Logger LOGGER = Logger.getLogger(CsvReaderFactory.class);

    public CSVReader createTsvReader(String tsvFilePath) {
        try {
            Path filePath = FileSystems.getDefault().getPath(checkNotNull(tsvFilePath));
            Reader dataFileReader = new InputStreamReader(Files.newInputStream(filePath));
            return createTsvReader(dataFileReader);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException("Error trying to open " + tsvFilePath, e);
        }
    }

    public static CSVReader createTsvReader(Reader source) {
        return new CSVReader(source, '\t');
    }
}