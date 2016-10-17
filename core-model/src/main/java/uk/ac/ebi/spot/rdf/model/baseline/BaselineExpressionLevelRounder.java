package uk.ac.ebi.spot.rdf.model.baseline;

import org.apache.commons.math.util.MathUtils;

public class BaselineExpressionLevelRounder {

    private BaselineExpressionLevelRounder(){}

    public static double round(double value) {
        int numberOfFractionalDigits;
        if (value >= 1) numberOfFractionalDigits = 0;
        else if (value >= 0.1) numberOfFractionalDigits = 1;
        else numberOfFractionalDigits = 7;
        return MathUtils.round(value, numberOfFractionalDigits);
    }

}
