package uk.ac.ebi.spot.rdf.model.baseline;

import uk.ac.ebi.spot.rdf.model.*;

import java.util.*;

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
public class BaselineExperiment extends Experiment {

    private ExperimentalFactors experimentalFactors;

    private AssayGroups assayGroups;

    public BaselineExperiment(String accession, Date lastUpdate, ExperimentalFactors experimentalFactors,
                       String description,
                       String displayName, Set<String> species, Map<String, String> speciesMapping,
                       boolean hasExtraInfoFile,
                       Set<String> pubMedIds, ExperimentDesign experimentDesign, AssayGroups assayGroups) {

        super(ExperimentType.RNASEQ_MRNA_BASELINE, accession, lastUpdate, displayName, description,
                hasExtraInfoFile, species, speciesMapping, pubMedIds, experimentDesign);
        this.experimentalFactors = experimentalFactors;
        this.assayGroups = assayGroups;
    }

    public Set<String> getExperimentRunAccessions() {
        return assayGroups.getAssayAccessions();
    }

    public ExperimentalFactors getExperimentalFactors() {
        return experimentalFactors;
    }

    public AssayGroups getAssayGroups() {
        return assayGroups;
    }

    public SortedSet<Factor> getAssayGroupFactors(Collection<String> assayGroupIds, String factorType) {
        return getExperimentalFactors().getFactorsForAssayGroupsByType(assayGroupIds, factorType);
    }


}

