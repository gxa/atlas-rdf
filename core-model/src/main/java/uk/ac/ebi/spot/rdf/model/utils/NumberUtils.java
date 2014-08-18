package uk.ac.ebi.spot.rdf.model.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.math.util.MathUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/*
 * Copyright 2008-2013 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */
public class NumberUtils {

    private static final int FRACTIONAL_DIGITS_FOR_VALUE_LARGER_OR_EQUAL_TO_ONE = 0;
    private static final int FRACTIONAL_DIGITS_FOR_VALUE_SMALLER_THAN_ONE = 1;

    // P-values less than 10E-10 are shown as '< 10^-10'
    private static final Double MIN_REPORTED_VALUE = 1E-10d;
    private static final String TEN = "10";
    private static final String MULTIPLY_HTML_CODE = " \u00D7 ";
    private static final String E_PATTERN = "#.##E0";
    private static final String E = "E";
    private static final String SUP_PRE = "<span style=\"vertical-align: super;\">";
    private static final String SUP_POST = "</span>";
    private static final int EXPONENT_MINIMUM = -3;

    private final NumberFormat format1Dp = NumberFormat.getNumberInstance();
    private final NumberFormat formatNoDp = NumberFormat.getNumberInstance();

    public NumberUtils() {
        format1Dp.setGroupingUsed(false);
        format1Dp.setMaximumFractionDigits(2);
        formatNoDp.setGroupingUsed(false);
        formatNoDp.setMaximumFractionDigits(0);
    }

    public String baselineExpressionLevelAsString(double expressionLevel) {
        return expressionLevel >= 1 ? formatNoDp.format(expressionLevel) : format1Dp.format(expressionLevel);
    }

    public double round(double value) {
        int numberOfFractionalDigits = value >= 1 ? FRACTIONAL_DIGITS_FOR_VALUE_LARGER_OR_EQUAL_TO_ONE
                : FRACTIONAL_DIGITS_FOR_VALUE_SMALLER_THAN_ONE;
        return round(value, numberOfFractionalDigits);
    }

    public String htmlFormatDoubleEncoded(double number) {
        return StringEscapeUtils.escapeHtml(htmlFormatDouble(number));
    }

    public double round(double number, int maxFractionDigits) {
        return MathUtils.round(number, maxFractionDigits);
    }

    public String formatDouble(double number) {
        return (number > 0 && number < MIN_REPORTED_VALUE) ? "<" + format2DpWithExponent(MIN_REPORTED_VALUE)
                : format2DpWithExponent(number);
    }

    public String htmlFormatDouble(double number) {
        return (number > 0 && number < MIN_REPORTED_VALUE) ? "<" + formatNumberHTML(MIN_REPORTED_VALUE)
                : formatNumberHTML(number);
    }

    String format2DpWithExponent(double number) {

        DecimalFormat df = new DecimalFormat(E_PATTERN);
        // Examples values of auxFormat: 6.2E-3, 0E0
        String auxFormat = df.format(number);

        // We now convert this format to 6.2*10^-3 (and 0 in the case of 0E0 specifically)
        String[] formatParts = auxFormat.split(E);
        int exponent = Integer.parseInt(formatParts[1]);
        if (exponent >= EXPONENT_MINIMUM && exponent <= 0) {
            return new DecimalFormat("#.###").format(number);
        }

        return auxFormat;
    }

    String formatNumberHTML(double number) {
        String formattedNumber = format2DpWithExponent(number);

        String[] formatParts = formattedNumber.split(E);

        if (formatParts.length == 1) {
            return formattedNumber;
        }

        String mantissa = formatParts[0];
        String exponent = formatParts[1];

        StringBuilder stringBuilder = new StringBuilder();

        if (!"1".equals(mantissa)) {
            stringBuilder.append(mantissa).append(MULTIPLY_HTML_CODE);
        }
        stringBuilder.append(TEN)
                .append(SUP_PRE)
                .append(exponent)
                .append(SUP_POST);
        return stringBuilder.toString();
    }

}