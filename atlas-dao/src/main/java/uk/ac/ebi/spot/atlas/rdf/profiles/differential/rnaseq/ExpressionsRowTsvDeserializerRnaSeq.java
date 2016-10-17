package uk.ac.ebi.spot.atlas.rdf.profiles.differential.rnaseq;

import com.google.common.collect.Iterables;
import uk.ac.ebi.spot.atlas.rdf.profiles.ExpressionsRowTsvDeserializer;
import uk.ac.ebi.spot.rdf.model.differential.Contrast;
import uk.ac.ebi.spot.rdf.model.differential.DifferentialExpression;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkState;

public class ExpressionsRowTsvDeserializerRnaSeq extends ExpressionsRowTsvDeserializer<DifferentialExpression> {

    private Iterator<Contrast> expectedContrasts;

    ExpressionsRowTsvDeserializerRnaSeq(List<Contrast> orderedContrasts) {
        this.expectedContrasts = Iterables.cycle(orderedContrasts).iterator();
    }

    public DifferentialExpression nextExpression(Queue<String> tsvRow) {
        String pValueString = tsvRow.poll();
        if (pValueString == null) {
            return null;
        }
        String foldChangeString = tsvRow.poll();
        checkState(foldChangeString != null, "missing fold change column in the analytics file");
        if ("NA".equalsIgnoreCase(pValueString) || "NA".equalsIgnoreCase(foldChangeString)) {
            expectedContrasts.next();
            return nextExpression(tsvRow);
        }
        double pValue = parseDouble(pValueString);
        double foldChange = parseDouble(foldChangeString);

        Contrast contrast = expectedContrasts.next();
        return new DifferentialExpression(pValue, foldChange, contrast);
    }

    double parseDouble(String value) {
        if (value.equalsIgnoreCase("inf")) {
            return Double.POSITIVE_INFINITY;
        }
        if (value.equalsIgnoreCase("-inf")) {
            return Double.NEGATIVE_INFINITY;
        }
        return Double.parseDouble(value);
    }


}
