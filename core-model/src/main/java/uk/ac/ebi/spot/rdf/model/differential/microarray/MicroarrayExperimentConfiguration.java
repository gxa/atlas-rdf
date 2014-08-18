package uk.ac.ebi.spot.rdf.model.differential.microarray;

import com.google.common.collect.Sets;
import org.apache.commons.configuration.XMLConfiguration;
import org.w3c.dom.Document;
import uk.ac.ebi.spot.rdf.model.ExperimentConfiguration;

import java.util.List;
import java.util.SortedSet;

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
public class MicroarrayExperimentConfiguration extends ExperimentConfiguration {

    private SortedSet<String> arrayDesignAccessions;

    public MicroarrayExperimentConfiguration(XMLConfiguration xmlConfiguration, Document document) {
        super(xmlConfiguration, document);
        this.arrayDesignAccessions = Sets.newTreeSet((List) xmlConfiguration.getList("analytics/array_design"));
    }

    public SortedSet<String> getArrayDesignAccessions() {
        return arrayDesignAccessions;
    }
}
