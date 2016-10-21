package uk.ac.ebi.atlas.model.differential;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import uk.ac.ebi.atlas.model.AssayGroup;

import java.util.Set;

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
public class Contrast implements Comparable<Contrast> {
    private String id;
    private String arrayDesignAccession; //used only for micro-array experiments
    private AssayGroup referenceAssayGroup;
    private AssayGroup testAssayGroup;
    private String displayName;

    public Contrast(String id, String arrayDesignAccession, AssayGroup referenceAssayGroup, AssayGroup testAssayGroup, String displayName) {
        this.id = id;
        this.arrayDesignAccession = arrayDesignAccession;
        this.referenceAssayGroup = referenceAssayGroup;
        this.testAssayGroup = testAssayGroup;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getArrayDesignAccession() {
        return arrayDesignAccession;
    }

    public AssayGroup getReferenceAssayGroup() {
        return referenceAssayGroup;
    }

    public AssayGroup getTestAssayGroup() {
        return testAssayGroup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getAssayAccessions(){
        return Sets.newHashSet(getReferenceAssayGroup().getFirstAssayAccession()
                , getTestAssayGroup().getFirstAssayAccession());
    }

    @Override
    public String toString() {
        return "Contrast{" +
                "id='" + id + '\'' +
                ", arrayDesignAccesion=" + arrayDesignAccession +
                ", referenceAssayGroup=" + referenceAssayGroup +
                ", testAssayGroup=" + testAssayGroup +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Contrast) {
            return Objects.equal(id, ((Contrast) other).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public int compareTo(Contrast o) {

        if (o == null) {
            return 1;
        }
        return this.getDisplayName().compareTo(o.getDisplayName());
    }
}
