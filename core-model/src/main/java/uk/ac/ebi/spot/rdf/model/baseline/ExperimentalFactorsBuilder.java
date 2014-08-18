package uk.ac.ebi.spot.rdf.model.baseline;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

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
public class ExperimentalFactorsBuilder {

    private String defaultQueryType;

    private Set<Factor> defaultFilterFactors;

    private Map<String, String> factorNamesByType = new HashMap<>();

    private List<FactorGroup> orderedFactorGroups;

    private Map<String, FactorGroup> orderedFactorGroupsByAssayGroup;

    private Set<String> menuFilterFactorTypes;

    public ExperimentalFactorsBuilder withDefaultQueryType(String defaultQueryType) {
        this.defaultQueryType = defaultQueryType;
        return this;
    }

    public ExperimentalFactorsBuilder withDefaultFilterFactors(Set<Factor> defaultFilterFactors) {
        this.defaultFilterFactors = defaultFilterFactors;
        return this;
    }

    public ExperimentalFactorsBuilder withFactorNamesByType(Map<String, String> factorNamesByType) {
        this.factorNamesByType = factorNamesByType;
        return this;
    }

    public ExperimentalFactorsBuilder withOrderedFactorGroups(List<FactorGroup> orderedFactorGroups) {
        this.orderedFactorGroups = orderedFactorGroups;
        return this;
    }

    public ExperimentalFactorsBuilder withOrderedFactorGroupsByAssayGroup(Map<String, FactorGroup> orderedFactorGroupsByAssayGroup) {
        this.orderedFactorGroupsByAssayGroup = orderedFactorGroupsByAssayGroup;
        return this;
    }

    public ExperimentalFactorsBuilder withMenuFilterFactorTypes(Set<String> menuFilterFactorTypes) {

        this.menuFilterFactorTypes = menuFilterFactorTypes;
        return this;
    }

    public ExperimentalFactors create() {
        checkState(menuFilterFactorTypes != null, "Please provide a set of menu filter factor types");
        checkState(StringUtils.isNotBlank(defaultQueryType), "Please provide a non blank defaultQueryType");
        checkState(defaultFilterFactors != null, "Please provide a set of filter factors");


        SortedSetMultimap<String, Factor> factorsByType = buildFactorsByType();
        SortedSetMultimap<Factor, Factor> coOccurringFactors = buildCoOccurringFactors();

        return new ExperimentalFactors(factorsByType, factorNamesByType, orderedFactorGroups,
                coOccurringFactors, menuFilterFactorTypes, orderedFactorGroupsByAssayGroup, defaultQueryType, defaultFilterFactors);
    }

    SortedSetMultimap<String, Factor> buildFactorsByType() {

        SortedSetMultimap<String, Factor> factorsByType = TreeMultimap.create();

        for (FactorGroup factorGroup : orderedFactorGroups) {

            for (Factor factor : factorGroup) {

                factorsByType.put(factor.getType(), factor);

            }
        }
        return factorsByType;
    }


    SortedSetMultimap<Factor, Factor> buildCoOccurringFactors() {

        SortedSetMultimap<Factor, Factor> coOccurringFactors = TreeMultimap.create();

        for (FactorGroup factorGroup : orderedFactorGroups) {

            for (Factor factor : factorGroup) {

                for (Factor value : factorGroup) {
                    if (!value.equals(factor)) {
                        coOccurringFactors.put(factor, value);
                    }
                }
            }
        }
        return coOccurringFactors;
    }

}
