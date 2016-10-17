package uk.ac.ebi.spot.atlas.rdf.profiles.baseline;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import uk.ac.ebi.spot.atlas.rdf.profiles.ExpressionsRowTsvDeserializer;
import uk.ac.ebi.spot.rdf.model.baseline.BaselineExpression;
import uk.ac.ebi.spot.rdf.model.baseline.FactorGroup;
import uk.ac.ebi.spot.rdf.model.baseline.QuartilesArrayBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class ExpressionsRowTsvDeserializerBaseline extends ExpressionsRowTsvDeserializer<BaselineExpression> {

    private static final String ZERO_CODE = "-";
    private static final double[] ZERO_QUARTILES = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};

    final int expectedNumberOfValues;
    Iterator<FactorGroup> factorGroups;

    public ExpressionsRowTsvDeserializerBaseline(List<FactorGroup> orderedFactorGroups) {
        expectedNumberOfValues = orderedFactorGroups.size();
        factorGroups = Iterables.cycle(orderedFactorGroups).iterator();
    }

    @Override
    public ExpressionsRowTsvDeserializer reload(String... values) {
        if (values.length != expectedNumberOfValues) {
            throw new IllegalArgumentException(String.format("Expected %s values but got [%s]", expectedNumberOfValues, Joiner.on(",").join(values)));
        }
        return super.reload(values);
    }

    @Override
    public BaselineExpression nextExpression(Queue<String> tsvRow) {
        String expressionLevelString = tsvRow.poll();

        if (expressionLevelString == null) {
            return null;
        }

        if (expressionLevelString.equals(ZERO_CODE)) {
            return new BaselineExpression(ZERO_QUARTILES, factorGroups.next());
        }

        if (expressionLevelString.contains(",")) {
            double[] quartiles = QuartilesArrayBuilder.create(expressionLevelString);
            return new BaselineExpression(quartiles, factorGroups.next());
        }

        return new BaselineExpression(expressionLevelString, factorGroups.next());
    }

}
