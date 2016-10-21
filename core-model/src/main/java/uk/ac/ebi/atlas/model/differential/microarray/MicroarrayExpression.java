package uk.ac.ebi.atlas.model.differential.microarray;

import com.google.common.base.Objects;
import uk.ac.ebi.atlas.model.differential.Contrast;
import uk.ac.ebi.atlas.model.differential.DifferentialExpression;

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
public class MicroarrayExpression extends DifferentialExpression {

    private double tstatistic;

    public MicroarrayExpression(double pValue, double foldChange, double tstatistic, Contrast contrast) {
        super(pValue, foldChange, contrast);
        this.tstatistic = tstatistic;
    }

    //It's used in jsp EL
    public double getTstatistic() {
        return tstatistic;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("p-value", getPValue())
                .add("foldChange", getFoldChange())
                .toString();
    }
}

