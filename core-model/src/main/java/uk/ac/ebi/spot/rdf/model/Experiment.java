package uk.ac.ebi.spot.rdf.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
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
public class Experiment implements Serializable {

    private ExperimentType type;
    private ExperimentDesign experimentDesign;
    private SortedSet<String> species;
    private SortedSet<String> pubMedIds;
    private Map<String, String> speciesMapping;
    private String accession;
    private String description;
    private String displayName;
    private boolean hasExtraInfoFile;
    private Date lastUpdate;

    public Experiment(ExperimentType type, String accession, Date lastUpdate, String displayName, String description,
                      boolean hasExtraInfoFile, Set<String> species, Map<String, String> speciesMapping, Set<String> pubMedIds, ExperimentDesign experimentDesign) {
        this.type = type;
        this.lastUpdate = lastUpdate;
        this.experimentDesign = experimentDesign;
        this.accession = accession;
        this.displayName = displayName;
        this.description = description;
        this.hasExtraInfoFile = hasExtraInfoFile;
        this.species = new TreeSet<String>(species);
        this.speciesMapping = speciesMapping;
        this.pubMedIds = Sets.newTreeSet(pubMedIds);
    }

    public Experiment(ExperimentType type, String accession, Date lastUpdate, String description, boolean hasExtraInfoFile,
                      Set<String> species, Map<String, String> speciesMapping, Set<String> pubMedIds, ExperimentDesign experimentDesign) {
        this(type, accession, lastUpdate, null, description, hasExtraInfoFile, species, speciesMapping, pubMedIds, experimentDesign);
    }

    public ExperimentType getType() {
        return type;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public ExperimentDesign getExperimentDesign() {
        return experimentDesign;
    }

    public String getDisplayName() {
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        return getAccession();
    }

    public String getDescription() {
        return description;
    }

    public boolean hasExtraInfoFile() {
        return hasExtraInfoFile;
    }

    public String getAccession() {
        return accession;
    }

    public Set<String> getSpecies() {
        return Collections.unmodifiableSet(species);
    }

    public List<String> getPubMedIds() {
        return Lists.newArrayList(pubMedIds);
    }

    public String getFirstSpecies() {
        return species.iterator().next();
    }

    public Map<String, String> getSpeciesMapping() {
        return Collections.unmodifiableMap(speciesMapping);
    }

    public String getRequestSpeciesName(String species) {
        String speciesName = speciesMapping.get(species);
        if (speciesName != null) {
            return Character.toUpperCase(speciesName.charAt(0)) + speciesName.substring(1);
        }
        return "";
    }

}